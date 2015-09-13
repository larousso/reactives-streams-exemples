package com.adelegue.reactivestreams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, FlowGraph, Merge, Sink, Source}
import akka.stream.{ActorMaterializer, UniformFanInShape, UniformFanOutShape}


object AkkaStreamGraph2 extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val alphabet = Source(List("a", "B", "c", "D", "e", "F", "G", "h", "i", "j", "K", "l", "M", "N", "o", "p", "q", "r", "s", "T", "U", "v", "W", "x", "y", "Z"))

  val splitGraph = FlowGraph.partial() { implicit builder =>

    import FlowGraph.Implicits._

    val bcast = builder.add(Broadcast[String](2))

    val out1 = bcast ~> Flow[String].map(_.toUpperCase)
    val out2 = bcast ~> Flow[String].map(_.toLowerCase)

    UniformFanOutShape(bcast.in, out1.outlet, out2.outlet)
  }.named("Minuscule_Majuscule")

  val mergeGraph = FlowGraph.partial() { implicit builder =>
    import FlowGraph.Implicits._

    val merge = builder.add(Merge[String](2))

    val concat = Flow[String].fold("")((acc, v) => acc + v)

    val concat1 = merge.in(0) <~ concat
    val concat2 = merge.in(1) <~ concat

    UniformFanInShape(merge.out, concat1.inlet, concat2.inlet)
  }.named("Concat_merge")

  val flow = Flow() { implicit builder =>
    import FlowGraph.Implicits._
    val split = builder.add(splitGraph)
    val merge = builder.add(mergeGraph)

    split.out(0) ~> merge.in(0)
    split.out(1) ~> merge.in(1)

    (split.in, merge.out)
  }.named("Combined")


  alphabet.via(flow).runWith(Sink.foreach(println))


  system.shutdown()
  system.awaitTermination()
}