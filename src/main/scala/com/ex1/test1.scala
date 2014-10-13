package org.ex1

import org.neo4j.graphdb.factory._

object Test1 {

  def main(args: Array[String]): Unit = {
    val graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/2neo");
    println("db running")
    graphDb.shutdown
    println("db shutdown")
  }

}

