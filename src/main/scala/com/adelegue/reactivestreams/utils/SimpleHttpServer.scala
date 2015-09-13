package com.adelegue.reactivestreams.utils
import akka.pattern._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directive.addByNameNullaryApply
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow

object SimpleHttpServer extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  
  val requestHandler = path("api" / Rest) { pathRest =>
     get {
       complete {
         after(1.second, system.scheduler)(Future.successful("Reponse " + pathRest))
       }
     }
  }
  
  Http().bindAndHandle(requestHandler, "localhost", 8080)
}