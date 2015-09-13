package com.adelegue.reactivestreams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast, Flow, FlowGraph, Merge, Sink, Source}

object AkkaStreamGraph extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()


  val graph = FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>
    import FlowGraph.Implicits._

    val alphabet = Source(List("a", "B", "c", "D", "e", "F", "G", "h", "i", "j", "K", "l", "M", "N", "o", "p", "q", "r", "s", "T", "U", "v", "W", "x", "y", "Z"))

    val bcast = builder.add(Broadcast[String](2))
    val merge = builder.add(Merge[String](2))
    val maj = Flow[String].map(_.toUpperCase)
    val min = Flow[String].map(_.toLowerCase)
    val print = Sink.foreach[String] {
      println
    }

    val concat = Flow[String].fold("")((acc, v) => acc + v)

    alphabet ~> bcast ~> maj ~> concat ~> merge ~> print
                bcast ~> min ~> concat ~> merge
  }

  graph.run()

  system.shutdown()
  system.awaitTermination()

}