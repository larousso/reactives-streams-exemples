package com.adelegue.reactivestreams
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

import scala.util.Random


object DownLoadFile extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system)
      .withInputBuffer(
        initialSize = 1,
        maxSize = 1))

  val alphabet = List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

  //curl --noproxy "*" --limit-rate 1k "http://localhost:8081/"

  val routes = path("") {
    get {
      complete {
        val stream = Source(() => Iterator from 0)
          .map(i => Random.nextInt(26))
          .map(alphabet)
          .grouped(26)
          .map(_.mkString)
          .map { x => println("Size of chunk : " + x.length); ChunkStreamPart(x)}
        HttpEntity.Chunked(MediaTypes.`application/octet-stream`, stream)
      }
    }
  }

  Http().bindAndHandle(routes, "localhost", 8081)
  
  System.in.read()
  system.shutdown()
  system.awaitTermination()
}