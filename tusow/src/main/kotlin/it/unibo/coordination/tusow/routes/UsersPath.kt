package it.unibo.coordination.tusow.routes

import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.tusow.api.UsersApi
import it.unibo.coordination.tusow.exceptions.BadContentError
import it.unibo.coordination.tusow.exceptions.HttpError
import it.unibo.coordination.tusow.presentation.Link
import it.unibo.coordination.tusow.presentation.User
import it.unibo.presentation.Deserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.MIMETypes.Companion.parse
import it.unibo.presentation.Serializer
import java.util.*
import java.util.stream.Collectors

class UsersPath : Path("/users") {
    override fun setupRoutes() {
        addRoute(HttpMethod.POST, Handler { post(it) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.GET, Handler { get(it) })
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.GET, "/:identifier", Handler { getUser(it) })
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.PUT, "/:identifier", Handler { putUser(it) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())
    }

    private fun getUsersMarshaller(mimeType: MIMETypes): Serializer<User> {
        return presentation.serializerOf(User::class.java, mimeType)
    }

    private fun getUsersUnmarshaller(mimeType: MIMETypes): Deserializer<User> {
        return presentation.deserializerOf(User::class.java, mimeType)
    }

    private fun getLinkMarshaller(mimeType: MIMETypes): Serializer<Link> {
        return presentation.serializerOf(Link::class.java, mimeType)
    }

    private fun getLinkUnmarshaller(mimeType: MIMETypes): Deserializer<Link> {
        return presentation.deserializerOf(Link::class.java, mimeType)
    }

    private fun post(routingContext: RoutingContext) {
        val api = UsersApi[routingContext]
        val result = Promise.promise<Link>()
        val mimeType = parse(routingContext.parsedHeaders().contentType().value())
        result.future().setHandler(responseHandler(routingContext, this::getLinkMarshaller))
        try {
            val user = getUsersUnmarshaller(mimeType).fromString(routingContext.bodyAsString)
            validateUserForPost(user)
            api.createUser(user, result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun validateUserForPost(user: User) {
        requireNoneIsNull(user.email, user.username, user.password)
        requireAllAreNull(user.id, user.link)
        user.id = UUID.randomUUID()
        user.setLinkUrl(getSubPath(user.username))
    }

    private operator fun get(routingContext: RoutingContext) {
        val api = UsersApi[routingContext]
        val result = Promise.promise<Collection<User>>()
        result.future().setHandler(responseHandlerWithManyContents(routingContext, this::getUsersMarshaller, this::cleanUsers))
        try {
            val skip = Optional.ofNullable(routingContext.queryParams()["skip"]).map { it.toInt() }
            val limit = Optional.ofNullable(routingContext.queryParams()["limit"]).map { it.toInt() }
            val filter = Optional.ofNullable(routingContext.queryParams()["filter"])
            api.readAllUsers(skip.orElse(0), limit.orElse(10), filter.orElse(""), result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun cleanUsers(list: Collection<User>): Collection<User> {
        return list.stream().map { u: User -> cleanUser(u) }.collect(Collectors.toList())
    }

    private fun getUser(routingContext: RoutingContext) {
        val api = UsersApi[routingContext]
        val result = Promise.promise<User>()
        result.future().setHandler(responseHandler(routingContext, this::getUsersMarshaller, this::cleanUser))
        try {
            val identifier = routingContext.pathParam("identifier")
            api.readUser(identifier, result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun putUser(routingContext: RoutingContext) {
        val api = UsersApi[routingContext]
        val mimeType = parse(routingContext.parsedHeaders().contentType().value())
        val result = Promise.promise<User>()
        result.future().setHandler(responseHandler<User>(routingContext, this::getUsersMarshaller, this::cleanUser))
        try {
            val user = getUsersUnmarshaller(mimeType).fromString(routingContext.bodyAsString) // = User.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateUserForPutUser(user)
            api.updateUser(routingContext.pathParam("identifier"), user, result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (   /*| IOException */e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun cleanUser(u: User): User {
        return User(u).setPassword(null)
    }

    private fun validateUserForPutUser(user: User) {
        requireAllAreNull(user.id, user.link, user.role)
    }
}