/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisrestclient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:Biopolis [biopolis3]<br>
 * USAGE:
 * <pre>
 *        BiopolisClient2 client = new BiopolisClient2();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author FITHIS
 */
public class BiopolisClient {
    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://31.22.112.71:8080/BiopolisWebApp";

    public BiopolisClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("biopolis3");
    }

    public <T> T packagesearchGalleriesExternal(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("packagesearch/galleries/{0}/external", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T viewGalleriesPackages(Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/galleries/{0}/packages", new Object[]{id})).request().post(null, responseType);
    }

    public <T> T putModel(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("model").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T createGallery(Object requestEntity, Class<T> responseType, String userid) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("users/{0}/galleries", new Object[]{userid})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T getGallery(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("galleries/{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getUser(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("users/{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getActivePackages(Class<T> responseType, String userid) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("users/{0}/packages", new Object[]{userid}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T addToGallery(Class<T> responseType, String id1, String id2) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("galleries/{0}/packages/{1}", new Object[]{id1, id2})).request().put(null, responseType);
    }

    public <T> T getGalleryOwningUser(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("galleries/{0}/user", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T postModel(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("model").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T searchResults(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("searchresults").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T packagesearchUsers(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("packagesearch/users/{0}/packages", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T createLibrary(Object requestEntity, Class<T> responseType, String userid) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("users/{0}/libraries", new Object[]{userid})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T termsearchUserPackages(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("termsearch/users/{0}/packages", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T viewUsersPackages(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/users/{0}/packages", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T viewLibrariesPackages(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/libraries/{0}/packages", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T viewUsersGalleries(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/users/{0}/galleries", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T getPackageOwningUser(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("packages/{0}/user", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T termsearchUserLibraries(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("termsearch/users/{0}/libraries", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T deleteModel(Class<T> responseType) throws ClientErrorException {
        return webTarget.path("model").request().delete(responseType);
    }

    public <T> T getModel(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("model");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getPackage(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("packages/{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T deleteLibrary(Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("libraries/{0}", new Object[]{id})).request().delete(responseType);
    }

    public <T> T termsearchUsers(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("termsearch/users").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T getEcho(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("echo");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T termsearchLibraries(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("termsearch/libraries").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T createPackage(Object requestEntity, Class<T> responseType, String userid) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("users/{0}/packages", new Object[]{userid})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T termsearchGalleries(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("termsearch/galleries").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T termsearchVertices(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("termsearch/vertices").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T packagesearchGalleriesInternal(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("packagesearch/galleries/{0}/internal", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T deletePackage(Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("packages/{0}", new Object[]{id})).request().delete(responseType);
    }

    public <T> T termsearchPackages(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("termsearch/packages").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T termsearchUserGalleries(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("termsearch/users{0}/galleries", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T suggest(Class<T> responseType) throws ClientErrorException {
        return webTarget.path("suggest").request().post(null, responseType);
    }

    public <T> T clean(Class<T> responseType) throws ClientErrorException {
        return webTarget.path("clean").request().post(null, responseType);
    }

    public <T> T getLibraryOwningUser(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("libraries/{0}/user", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getLibrary(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("libraries/{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getActiveLibraries(Class<T> responseType, String userid) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("users/{0}/libraries", new Object[]{userid}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T deleteGallery(Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("galleries/{0}", new Object[]{id})).request().delete(responseType);
    }

    public <T> T viewUsersLibraries(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/users/{0}/libraries", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T getActiveGalleries(Class<T> responseType, String userid) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("users/{0}/galleries", new Object[]{userid}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T removeFromGallery(Class<T> responseType, String id1, String id2) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("galleries/{0}/packages/{1}", new Object[]{id1, id2})).request().delete(responseType);
    }

    public <T> T deleteUser(Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("users/{0}", new Object[]{id})).request().delete(responseType);
    }

    public <T> T packagesearchPackages(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("packagesearch/packages").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T viewPackagesGalleries(Object requestEntity, Class<T> responseType, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("view/packages/{0}/galleries", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T createUser(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("users").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public <T> T queryPackages(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.path("packages/query").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), responseType);
    }

    public void close() {
        client.close();
    }
    
}
