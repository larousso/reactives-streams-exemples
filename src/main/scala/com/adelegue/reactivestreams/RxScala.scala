package com.adelegue.reactivestreams

import rx.lang.scala.Observable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await

object RxScala extends App {

  println("Starting")

  Observable.from(1 to 10)
    .filter(i => i % 2 == 0)
    .map(_.toString)
    .concatMap(id => Observable.from(getFromAsyncApi(id)))
    .subscribe(
      str => println(str),
      err => println(s"Erreur $err"),
      () => println("Fini"))

  def getFromAsyncApi(id: String): Future[String] = Future {
    Thread.sleep(1000)
    s"From api $id"
  }
}