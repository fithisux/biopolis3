/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolisdata.queries.BiopolisTermQuery;
import biopolis.exceptions.system.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author vanag
 */
public class BiopolisUtilities {

    public static String makeContext(BiopolisTermQuery tq) throws BiopolisGeneralException {
        if (tq == null) {
            throw new BiopolisGeneralException("cannot make context without query");
        }
        if ((tq.c_keywords == null) && (tq.t_keywords == null)) {
            throw new BiopolisGeneralException("cannot make context with empty query");
        }
        String s1 = BiopolisUtilities.makeFieldContext("title", tq.t_keywords);
        String s2 = BiopolisUtilities.makeFieldContext("comments", tq.c_keywords);
        String comp = s1;
        if (!s1.isEmpty() && !s2.isEmpty()) {
            comp += (" AND " + s2);
        } else {
            comp += s2;
        }

        if (comp.isEmpty()) {
            throw new BiopolisGeneralException("cannot make context with empty query");
        }
        return comp;
    }

    public static String makeFieldContext(String namefield, String[] keywords) {
        if ((keywords == null) || (keywords.length == 0)) {
            return "";
        }

        String comp = namefield + ":" + keywords[0] + " ";
        for (int i = 1; i < keywords.length; i++) {
            comp += " AND " + namefield + ":" + keywords[i];
        }

        return comp;
    }

    public static String toNeo4JArglist(Long[] coreids) {
        String nodelist = "";
        if ((coreids != null) && (coreids.length != 0)) {
            boolean first = true;
            nodelist = "[";
            for (Long tag : coreids) {
                if (first) {
                    nodelist += Long.toString(tag);
                    first = false;
                } else {
                    nodelist += ("," + Long.toString(tag));
                }
            }
            nodelist += "]";
        }
        return nodelist;
    }
}
