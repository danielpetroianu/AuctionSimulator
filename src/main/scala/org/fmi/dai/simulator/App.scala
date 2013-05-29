package org.fmi.dai.simulator

import org.fmi.dai.config.ZeroLoggerFactory
import scala.actors.Actor
import java.util.Date

object App {

    implicit val (logger, formatter, appender) = ZeroLoggerFactory.newLogger(this)

    def main(args: Array[String]) {
        val minBid = 100
        val closing = new Date(new Date().getTime() + 4000)
        val seller = Actor.actor {}

        val actionHouse = new AuctionHouse(seller, minBid, closing)

        seller.start()
        actionHouse.start()

        new Buyer("Ion", 20, 100, actionHouse).start()
        new Buyer("Vasile", 30, 300, actionHouse).start()
    }
}
