/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ntua.biopolis;

import biopolis.exceptions.system.BiopolisGeneralException;
import biopolis.headless.BiopolisPersistencyLayer;
import biopolis.headless.BiopolisProperties;
import java.sql.SQLException;

/**
 *
 * @author FITHIS
 */
public class BiopolisInitializer {

    BiopolisPersistencyLayer pl = null;
    BiopolisProperties props = null;

    public BiopolisInitializer() {
        System.out.println("Initialization fuck");
        this.props = new BiopolisProperties();
        //props.filesystem_endpoint="c:/viopolis/biopolisdata";
        props.mongo_host = "mongodb://localhost:27019";
        props.neo4j_endpoint = "localhost:7474/db/data";
        props.segment_size = 20;
        props.expire_search = 1800;
        try {
            this.pl = new BiopolisPersistencyLayer(props);
        } catch (SQLException e1) {
            e1.printStackTrace();
            this.pl = null;
        } catch (BiopolisGeneralException e2) {
            e2.printStackTrace();
            this.pl = null;
        }
    }

}
