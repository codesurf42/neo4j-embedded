package org.ex1

import org.neo4j.graphdb.factory._
import org.neo4j.graphdb.{DynamicRelationshipType, GraphDatabaseService}

object App {

  def main(args: Array[String]): Unit = {
    implicit val graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/2neo");
    println("db running")
    createNodes

    graphDb.shutdown
    println("db shutdown")
  }

  def createNodes(implicit graphDb: GraphDatabaseService): Unit = {
    for (t <- 1 to 1000) {
      val tx = graphDb.beginTx()
      print(s"t: $t ")
      try {
        for (j <- 1 to 100) {
          val firstNode = graphDb.createNode()
          firstNode.setProperty("node", s"root $j")

          for (i <- 1 to 100) {
            val subNode = graphDb.createNode()
            subNode.setProperty("node", s"$j-$i")
            firstNode.createRelationshipTo(subNode, DynamicRelationshipType.withName("state"))
          }
        }
        tx.success()
      } finally {
        tx.close()
      }

    }
  }


}

