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

  case class Timings(time: Long, nodes: Int)

  def createNodes(implicit graphDb: GraphDatabaseService): Unit = {
    var totalNodes = 0
    val stats = collection.mutable.Map[Int, Timings]()
    for (t <- 1 to 4) {
      print(s"t: $t ")
      for (howNested <- List(1, 3, 5, 7, 10, 20, 50)) {
        var nodes = 0
        val start = System.currentTimeMillis()

        for (j <- 1 to 100) {
          val tx = graphDb.beginTx()
          try {
            val firstNode = graphDb.createNode()
            firstNode.setProperty("node", s"root $j")

            for (i <- 1 to 100 * howNested) {
              val subNode = graphDb.createNode()
              subNode.setProperty("node", s"$j-$i")
              firstNode.createRelationshipTo(subNode, DynamicRelationshipType.withName("state"))

              nodes += 1
            }
            tx.success()
          } finally {
            tx.close()
          }
        }

        val offset = System.currentTimeMillis() - start
        stats += (howNested -> stats.
          get(howNested).
          fold(Timings(offset, nodes))(v =>
          Timings(v.time + offset, v.nodes + nodes)
          ))
        totalNodes += nodes

      }
    }

    println()
    println("All nodes: " + totalNodes)
    stats.keys.toList.sorted.foreach { key =>
      val times = stats.get(key).get
      printf("Leafs: % 7d Time[sec]: % 8.3f Nodes: % 10d Nodes/sec: % 4d\n",
        key * 100,
        (times.time / 1000.0),
        times.nodes,
        (times.nodes / times.time))
    }
  }


}

