package com.adelegue.reactivestreams

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer


object AkkaHttp extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val requestHandler = path("") {
    get {
      complete {
        <h1>Welcome to the home page</h1>
      }
    }
  } ~ path("hello") {
    get {
      complete {
        <body>
          <h1>Welcome to the hello page</h1>
          <form action="/hello" method="post">
            <button type="submit">Valider</button>
          </form>
        </body>
      }
    } ~
    post {
      redirect("/", StatusCodes.MovedPermanently)
    }
  } ~ path("assets") {
      getFromBrowseableDirectory("Users/adelegue/http")
  }

  Http().bindAndHandle(requestHandler, "localhost", 8082)

}
