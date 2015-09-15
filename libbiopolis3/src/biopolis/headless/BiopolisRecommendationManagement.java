/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolis.exceptions.system.BiopolisRecommendationException;
import biopolis.exceptions.system.*;
import biopolisdata.*;
import biopolisdata.topological.*;
import biopolisdata.topological.exceptions.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.*;
import java.io.IOException;
import javax.ws.rs.core.*;
import com.sun.jersey.api.client.*;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.jdbc.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author vanag
 */
public class BiopolisRecommendationManagement {

    BiopolisGraphManagement bgr;
    float[] weights;
    BiopolisNode[] treerep;

    public BiopolisRecommendationManagement(BiopolisGraphManagement bgr) throws SQLException, BiopolisInactiveException, BiopolisModelStructureException {
        this.bgr = bgr;
        WSResult<BiopolisModel> result = this.bgr.describeModel();
        BiopolisModel model = result.result[0];
        BiopolisSortedModel smodel = new BiopolisSortedModel(model);
        this.weights = new float[smodel.sortedvertices.length];
        this.treerep = smodel.treerep;
    }

    public List<BiopolisWeightedTag> suggest(BiopolisRecommendationRequest req) throws SQLException, BiopolisGeneralException, BiopolisInactiveException, BiopolisModelStructureException, BiopolisRecommendationException {

        if ((req.Ntags <= 0) || (req.similarities == null) || (req.similarities.length == 0)) {
            return new ArrayList<BiopolisWeightedTag>();
        }
        Map<Long, Float> mapping = new HashMap<Long, Float>();
        for (BiopolisSimilarity recommendation : req.similarities) {
            mapping.put(recommendation.pkgid, recommendation.weight);
        }
        Long[] lista = mapping.keySet().toArray(new Long[0]);
        List<BiopolisWeightedTag> wtags = new ArrayList<BiopolisWeightedTag>();
        List<BiopolisResult<BiopolisPackage>> pkgs = this.bgr.p_mgmnt.get(lista);
        for (BiopolisResult<BiopolisPackage> pkg : pkgs) {
            for (Long j : pkg.object.tags) {
                BiopolisWeightedTag wtag = new BiopolisWeightedTag();
                wtag.tag = j.intValue();
                wtag.weight = mapping.get(new Long(pkg.id));
                wtags.add(wtag);
            }
        }

        this.makeWeights(wtags);
        wtags = this.recommend();
        if (req.Ntags < 0) {
            throw new BiopolisRecommendationException("Bound for tags cannot be negative");
        }
        return wtags.subList(0, req.Ntags);
    }

    public void makeWeights(List<BiopolisWeightedTag> wtags) throws BiopolisRecommendationException {
        Arrays.fill(this.weights, 0);
        for (BiopolisWeightedTag wtag : wtags) {
            if ((wtag.tag < 0) || (wtag.tag >= weights.length)) {
                throw new BiopolisRecommendationException("Illegal tag,  node = " + wtag.tag);
            }

            if ((wtag.weight < 0) || (wtag.weight > 1)) {
                throw new BiopolisRecommendationException("non-normalized weight,  node = " + wtag.tag);
            }
            this.weights[wtag.tag] += wtag.weight;
        }

        this.propagateRecommendation();
    }

    public void propagateRecommendation() {
        //we start from children
        int[] found = new int[this.treerep.length];
        for (int i = 0; i < this.treerep.length; i++) {
            found[i] = this.treerep[i].children.size();
        }

        int events = 0;
        do {
            events = 0;
            //find children
            for (int i = 0; i < found.length; i++) {
                if (found[i] == 0) //we found a child
                {
                    for (BiopolisNode bn : this.treerep[i].parents) {
                        found[bn.coreid]--;
                        this.weights[bn.coreid] += this.weights[i];
                    }
                    found[i] = -1;
                    events++;
                }
            }
        } while (events > 0);
    }

    public List<BiopolisWeightedTag> recommend() {
        List<BiopolisWeightedTag> wtags = new ArrayList<BiopolisWeightedTag>();
        for (int i = 0; i < this.weights.length; i++) {
            BiopolisWeightedTag wtag = new BiopolisWeightedTag();
            wtag.tag = i;
            wtag.weight = weights[i];
            wtags.add(wtag);
        }
        Collections.sort(wtags);
        return wtags;
    }
}
