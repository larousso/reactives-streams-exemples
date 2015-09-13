package com.adelegue.reactivestreams

import akka.stream.scaladsl.Source
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object AkkaStream extends App {
  
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val alphabet = List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
  
  val source = Source(0 to 49)
  
  val alphabetFlow = Flow[Int].map(_ % 26).map {alphabet}
  val alphabetMajFlow = Flow[String].map { _.toUpperCase }.log("Maj ", s => s)
   
  
  val concatString = Flow[String].fold("")( (acc, current) => acc + current ).log("Concat", s => s)
  val count = Flow[String].mapConcat { _.split("").toList }.fold(0)( (acc, _) => acc + 1 )
  
  val source2 = source.via(alphabetFlow).via(alphabetMajFlow).via(concatString).via(count)
  
  source2.runForeach { println }
  
  system.shutdown()
  system.awaitTermination()
}