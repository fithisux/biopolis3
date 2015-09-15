package com.ntua.biopolis;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import biopolis.exceptions.system.BiopolisRecommendationException;
import biopolisdata.*;

import java.util.*;
import java.io.*;
import javax.ws.rs.*;
import java.net.URISyntaxException;
import biopolisdata.topological.exceptions.*;
import biopolis.exceptions.system.*;
import biopolis.headless.*;
import biopolisdata.queries.*;
import org.apache.commons.codec.binary.Base64;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ejb.*;

/**
 * REST Web Service
 *
 * @author Manolis
 */
@Path("biopolis3")
@Stateless
public class Biopolis extends Throwable {

    private static final int IMG_WIDTH = 100;
    private static final int IMG_HEIGHT = 100;
    private static BiopolisInitializer bi = new BiopolisInitializer();

    BiopolisGraphManagement graphManagement;

    public Biopolis() throws URISyntaxException, SQLException, BiopolisGeneralException {
        graphManagement = new BiopolisGraphManagement(Biopolis.bi.pl);
    }

    @GET
    @Produces({"application/json"})
    @Path(value = "/model")
    public WSResult<BiopolisModel> getModel() throws URISyntaxException {
        try {
            return this.graphManagement.describeModel();
        } catch (SQLException ex) {
            return new WSResult<BiopolisModel>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisInactiveException ex) {
            return new WSResult<BiopolisModel>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/model")
    public WSResult<String> postModel(BiopolisModel model) {
        try {
            graphManagement.updateModel(model);
        } catch (BiopolisModelStructureException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisModelExtensionException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisInactiveException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
        return new WSResult<String>("OK");
    }

    @POST
    @Produces("application/json")
    @Path(value = "/clean")
    public WSResult<String> clean() {
        return this.deleteModel();
    }

    @DELETE
    @Produces("application/json")
    @Path(value = "/model")
    public WSResult<String> deleteModel() {
        try {
            List<Long> lista = this.graphManagement.p_mgmnt.getAll();
            for (Long id : lista) {
                BiopolisPersistencyLayer.deleteJPEGAtLeoFS(id + ".jpg");
                BiopolisPersistencyLayer.deleteJPEGAtLeoFS("thumb_" + id + ".jpg");
            }
            graphManagement.reset();
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
        return new WSResult<String>("OK");
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/model")
    public WSResult<String> putModel(BiopolisModel model) {
        try {
            graphManagement.putModel(model);
        } catch (BiopolisModelStructureException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisActiveException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
        return new WSResult<String>("OK");
    }

    @GET
    @Produces("application/json")
    @Path(value = "/echo")
    public WSResult<String> getEcho() {
        return new WSResult<String>("ECHO");
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/users")
    public WSResult<Long> createUser(BiopolisUser user) {
        try {
            BiopolisUser[] users = new BiopolisUser[1];
            users[0] = user;
            long l = this.graphManagement.u_mgmnt.put(users)[0];
            return new WSResult<Long>(l);
        } catch (SQLException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @DELETE
    @Produces({"application/json"})
    @Path(value = "/users/{id}")
    public WSResult<String> deleteUser(@PathParam("id") long id) {
        return this.graphManagement.u_mgmnt.deleteIT(id);
    }

    @GET
    @Produces({"application/json"})
    @Path(value = "/users/{id}")
    public WSResult<BiopolisResult<BiopolisUser>> getUser(@PathParam("id") long id) {
        return this.graphManagement.u_mgmnt.getIT(id);
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/users/{userid}/packages")
    public WSResult<Long> createPackage(@PathParam("userid") long userid, BiopolisImage payload) {
        BiopolisPackage[] payloads = new BiopolisPackage[1];
        payloads[0] = payload.metadata;
        try {
            Long id = this.graphManagement.p_mgmnt.createIT(userid, payloads);
            try {
                byte[] decoded = Base64.decodeBase64(payload.data);
                BiopolisPersistencyLayer.putJPEGAtLeoFS(decoded, id.toString() + ".jpg");
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(decoded));
                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                BufferedImage resizeImageJpg = resizeImage(originalImage, type);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizeImageJpg, "jpg", baos);
                BiopolisPersistencyLayer.putJPEGAtLeoFS(baos.toByteArray(), "thumb_" + id.toString() + ".jpg");
            } catch (IOException ex) {
                return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
            }
            return new WSResult<Long>(id);
        } catch (BiopolisGeneralException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    @DELETE
    @Produces({"application/json"})
    @Path(value = "/packages/{id}")
    public WSResult<String> deletePackage(@PathParam("id") long id) {
        WSResult<String> mesg = this.graphManagement.p_mgmnt.deleteIT(id);
        BiopolisPersistencyLayer.deleteJPEGAtLeoFS(id + ".jpg");
        BiopolisPersistencyLayer.deleteJPEGAtLeoFS("thumb_" + id + ".jpg");
        return mesg;
    }

    @GET
    @Produces({"application/json"})
    @Path(value = "/packages/{id}")
    public WSResult<BiopolisResult<BiopolisPackage>> getPackage(@PathParam("id") long id) {
        return this.graphManagement.p_mgmnt.getIT(id);
    }

    @GET
    @Produces("application/json")
    @Path(value = "/users/{userid}/packages")
    public WSResult<BiopolisResult<BiopolisPackage>> getActivePackages(@PathParam("userid") long userid) {
        return this.graphManagement.p_mgmnt.getActive(userid);
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/users/{userid}/libraries")
    public WSResult<Long> createLibrary(@PathParam("userid") int userid, BiopolisLibrary payload) {
        BiopolisLibrary[] payloads = new BiopolisLibrary[1];
        payloads[0] = payload;
        try {
            Long id = this.graphManagement.l_mgmnt.createIT(userid, payloads);
            return new WSResult<Long>(id);
        } catch (BiopolisGeneralException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @DELETE
    @Produces({"application/json"})
    @Path(value = "/libraries/{id}")
    public WSResult<String> deleteLibrary(@PathParam("id") long id) {
        return this.graphManagement.l_mgmnt.deleteIT(id);
    }

    @GET
    @Produces({"application/json"})
    @Path(value = "/libraries/{id}")
    public WSResult<BiopolisResult<BiopolisLibrary>> getLibrary(@PathParam("id") long id) {
        return this.graphManagement.l_mgmnt.getIT(id);
    }

    @GET
    @Produces("application/json")
    @Path(value = "/users/{userid}/libraries")
    public WSResult<BiopolisResult<BiopolisLibrary>> getActiveLibraries(@PathParam("userid") long userid) {
        return this.graphManagement.l_mgmnt.getActive(userid);
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/users/{userid}/galleries")
    public WSResult<Long> createGallery(@PathParam("userid") int userid, BiopolisGallery payload) {
        BiopolisGallery[] payloads = new BiopolisGallery[1];
        payloads[0] = payload;
        try {
            Long id = this.graphManagement.g_mgmnt.createIT(userid, payloads);
            return new WSResult<Long>(id);
        } catch (BiopolisGeneralException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<Long>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @DELETE
    @Produces({"application/json"})
    @Path(value = "/galleries/{id}")
    public WSResult<String> deleteGallery(@PathParam("id") long id) {
        return this.graphManagement.g_mgmnt.deleteIT(id);
    }

    @GET
    @Produces({"application/json"})
    @Path(value = "/galleries/{id}")
    public WSResult<BiopolisResult<BiopolisGallery>> getGallery(@PathParam("id") long id) {
        return this.graphManagement.g_mgmnt.getIT(id);
    }

    @GET
    @Produces("application/json")
    @Path(value = "/users/{userid}/galleries")
    public WSResult<BiopolisResult<BiopolisGallery>> getActiveGalleries(@PathParam("userid") long userid) {
        return this.graphManagement.g_mgmnt.getActive(userid);
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path(value = "/searchresults")
    public WSResult<Object> searchResults(BiopolisSegmentQuery segquery) {
        try {
            List<Long> ll = this.graphManagement.seg_mgmnt.search(segquery);
            Long[] ids = ll.toArray(new Long[0]);
            if (segquery.nodetype.equalsIgnoreCase("PACKAGE_NODE")) {
                return new WSResult<Object>(this.graphManagement.p_mgmnt.get(ids));
            } else if (segquery.nodetype.equalsIgnoreCase("MODEL_NODE")) {
                return new WSResult<Object>(this.graphManagement.v_mgmnt.get(ids));
            } else if (segquery.nodetype.equalsIgnoreCase("USER_NODE")) {
                return new WSResult<Object>(this.graphManagement.u_mgmnt.get(ids));
            } else if (segquery.nodetype.equalsIgnoreCase("LIBRARY_NODE")) {
                return new WSResult<Object>(this.graphManagement.l_mgmnt.get(ids));
            } else if (segquery.nodetype.equalsIgnoreCase("GALLERY_NODE")) {
                return new WSResult<Object>(this.graphManagement.g_mgmnt.get(ids));
            } else {
                return new WSResult<Object>("Exception", "Cast problem");
            }
        } catch (SQLException ex) {
            return new WSResult<Object>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<Object>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/users")
    public WSResult<BiopolisSegmentation> termsearchUsers(BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.u_mgmnt.queryTerm(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/libraries")
    public WSResult<BiopolisSegmentation> termsearchLibraries(BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.l_mgmnt.queryTerm(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/galleries")
    public WSResult<BiopolisSegmentation> termsearchGalleries(BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.queryTerm(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/packages")
    public WSResult<BiopolisSegmentation> termsearchPackages(BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.p_mgmnt.queryTerm(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/vertices")
    public WSResult<BiopolisSegmentation> termsearchVertices(BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.v_mgmnt.queryTerm(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/users/{id}/packages")
    public WSResult<BiopolisSegmentation> termsearchUserPackages(@PathParam("id") long id, BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.v_mgmnt.queryUserTerm(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/users{id}/galleries")
    public WSResult<BiopolisSegmentation> termsearchUserGalleries(@PathParam("id") long id, BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.queryUserTerm(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/termsearch/users/{id}/libraries")
    public WSResult<BiopolisSegmentation> termsearchUserLibraries(@PathParam("id") long id, BiopolisTermQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.l_mgmnt.queryUserTerm(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/packages/query")
    public WSResult<BiopolisResult<BiopolisPackage>> queryPackages(BiopolisPackageQuery tq) {
        try {
            BiopolisSegmentation seg = this.graphManagement.p_mgmnt.queryPackages(tq);
            BiopolisSegmentQuery seg_q = new BiopolisSegmentQuery();
            seg_q.nodetype = seg.nodetype;
            seg_q.biopolisid = seg.biopolisid;
            seg_q.from = 0;
            List<Long> ll = this.graphManagement.seg_mgmnt.search(seg_q);
            Long[] ids = ll.toArray(new Long[0]);
            return new WSResult<BiopolisResult<BiopolisPackage>>(this.graphManagement.p_mgmnt.get(ids));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<BiopolisPackage>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<BiopolisPackage>>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/packagesearch/packages")
    public WSResult<BiopolisSegmentation> packagesearchPackages(BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.p_mgmnt.queryPackages(tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/packagesearch/galleries/{id}/internal")
    public WSResult<BiopolisSegmentation> packagesearchGalleriesInternal(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.queryGalleryInternal(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/packagesearch/galleries/{id}/external")
    public WSResult<BiopolisSegmentation> packagesearchGalleriesExternal(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.queryGalleryExternal(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/packagesearch/users/{id}/packages")
    public WSResult<BiopolisSegmentation> packagesearchUsers(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.u_mgmnt.queryUsersPackages(id, tq));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/view/packages/{id}/galleries")
    public WSResult<BiopolisSegmentation> viewPackagesGalleries(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.p_mgmnt.getGalleries(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Produces({"application/json"})
    @Path(value = "/view/galleries/{id}/packages")
    public WSResult<BiopolisSegmentation> viewGalleriesPackages(@PathParam("id") long id) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.getPackages(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/view/libraries/{id}/packages")
    public WSResult<BiopolisSegmentation> viewLibrariesPackages(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.l_mgmnt.getPackages(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/view/users/{id}/packages")
    public WSResult<BiopolisSegmentation> viewUsersPackages(@PathParam("id") long id) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.p_mgmnt.getFromUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/view/users/{id}/libraries")
    public WSResult<BiopolisSegmentation> viewUsersLibraries(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.l_mgmnt.getFromUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    @Path(value = "/view/users/{id}/galleries")
    public WSResult<BiopolisSegmentation> viewUsersGalleries(@PathParam("id") long id, BiopolisPackageQuery tq) {
        try {
            return new WSResult<BiopolisSegmentation>(this.graphManagement.g_mgmnt.getFromUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisSegmentation>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @GET
    @Produces("application/json")
    @Path(value = "/packages/{id}/user")
    public WSResult<BiopolisResult<BiopolisUser>> getPackageOwningUser(@PathParam("id") int id) {
        try {
            return new WSResult<BiopolisResult<BiopolisUser>>(this.graphManagement.p_mgmnt.getUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @GET
    @Produces("application/json")
    @Path(value = "/libraries/{id}/user")
    public WSResult<BiopolisResult<BiopolisUser>> getLibraryOwningUser(@PathParam("id") int id) {
        try {
            return new WSResult<BiopolisResult<BiopolisUser>>(this.graphManagement.l_mgmnt.getUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @GET
    @Produces("application/json")
    @Path(value = "/galleries/{id}/user")
    public WSResult<BiopolisResult<BiopolisUser>> getGalleryOwningUser(@PathParam("id") int id) {
        try {
            return new WSResult<BiopolisResult<BiopolisUser>>(this.graphManagement.g_mgmnt.getUser(id));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<BiopolisUser>>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @DELETE
    @Produces("application/json")
    @Path(value = "/galleries/{id1}/packages/{id2}")
    public WSResult<String> removeFromGallery(@PathParam("id1") long id1,
            @PathParam("id2") long id2) {
        try {
            Long[] pkgids = {id2};
            this.graphManagement.g_mgmnt.removePackages(id1, pkgids);
            return new WSResult<String>("OK");
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @PUT
    @Produces("application/json")
    @Path(value = "/galleries/{id1}/packages/{id2}")
    public WSResult<String> addToGallery(@PathParam("id1") long id1,
            @PathParam("id2") long id2) {
        try {
            Long[] pkgids = {id2};
            this.graphManagement.g_mgmnt.addPackages(id1, pkgids);
            return new WSResult<String>("OK");
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @POST
    @Produces("application/json")
    @Path(value = "/suggest")
    public WSResult<BiopolisWeightedTag> suggest(BiopolisRecommendationRequest req) {
        try {
            BiopolisRecommendationManagement r_mgmnt = new BiopolisRecommendationManagement(this.graphManagement);
            return new WSResult<BiopolisWeightedTag>(r_mgmnt.suggest(req));
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisWeightedTag>(ex.getClass().getName(), ex.getMessage());
        } catch (SQLException ex) {
            return new WSResult<BiopolisWeightedTag>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisInactiveException ex) {
            return new WSResult<BiopolisWeightedTag>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisModelStructureException ex) {
            return new WSResult<BiopolisWeightedTag>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisRecommendationException ex) {
            return new WSResult<BiopolisWeightedTag>(ex.getClass().getName(), ex.getMessage());
        }
    }

    @GET
    @Path("/packages/{id}/image")
    @Produces("image/jpeg")
    public Response getFullImage(@PathParam("id") long id) {

        try {
            byte[] imageData = BiopolisPersistencyLayer.getJPEGAtLeoFS(Long.toString(id) + ".jpg");
            return Response.ok(new ByteArrayInputStream(imageData)).build();
        } catch (IOException ex) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/packages/{id}/thumbnail")
    @Produces("image/jpeg")
    public Response getThumbnail(@PathParam("id") long id) {

        try {
            byte[] imageData = BiopolisPersistencyLayer.getJPEGAtLeoFS("thumb_" + Long.toString(id) + ".jpg");
            return Response.ok(new ByteArrayInputStream(imageData)).build();
        } catch (IOException ex) {
            return Response.serverError().build();
        }
    }

}
