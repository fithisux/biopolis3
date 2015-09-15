/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolis.exceptions.system.BiopolisGeneralException;
import java.util.ArrayList;
import java.util.List;
import biopolisdata.queries.*;
import com.mongodb.*;

/**
 *
 * @author Manolis
 */
public class BiopolisSpatialManagement {

    DBCollection dbcol;
    BiopolisGraphManagement bgr;

    public BiopolisSpatialManagement(BiopolisPersistencyLayer somepl) throws BiopolisGeneralException {
        dbcol = somepl.getDbcolPlaces();
    }

    public void addPlace(Long name, final double[] location) {
        final BasicDBObject place = new BasicDBObject();
        place.put("biopolisdata", name);
        place.put("loc", location);
        dbcol.insert(place);
    }

    public void removePlace(Long name) {
        dbcol.remove(new BasicDBObject("biopolisdata", name));
    }

    /*   
     public List<Long> queryPackagesSpatial(BiopolisSpatialQuery sq)
     {
     System.out.println("findCenterSphere\n----------------------\n");
     List circle = new ArrayList();
     circle.add(new double[] { sq.longitude, sq.latitude }); // Centre of circle
     circle.add(sq.radius/6.371); // Radius
     BasicDBObject query = new BasicDBObject("loc", new BasicDBObject("$geoWithin",
     new BasicDBObject("$centerSphere", circle)));
     DBCursor cur=dbcol.find(query);
     List<Long> lista=new ArrayList<Long>();
     while(cur.hasNext())
     {
     Long name = (Long) cur.next().get("biopolisdata");
     lista.add(name);
     }
     return lista;
     }
     */
    public DBCursor queryPackagesSpatial(BiopolisSpatialQuery sq) {
        System.out.println("findCenterSphere\n----------------------\n");
        List circle = new ArrayList();
        circle.add(new double[]{sq.longitude, sq.latitude}); // Centre of circle
        circle.add(sq.radius / 6.371); // Radius
        BasicDBObject query = new BasicDBObject("loc", new BasicDBObject("$geoWithin",
                new BasicDBObject("$centerSphere", circle)));
        return dbcol.find(query);
    }

    public void clean() {
        dbcol.remove(new BasicDBObject());
    }

}
