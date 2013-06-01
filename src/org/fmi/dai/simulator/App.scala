package org.fmi.dai.simulator

import scala.actors.Actor
import java.util.Date
import org.fmi.dai.config.AuctionConfig

object App {
    val random = new scala.util.Random

    def randomIncrement(min: Int, max: Int): Int = min + random.nextInt(max - min)

    def main(args: Array[String]) {
        val startBid = 100
        val closing = new Date(new Date().getTime() + AuctionConfig.AUCTION_TIME)

        new AuctionEngine(
            startBid,
            closing,
            new Seller("John Doe"),
            List(
                new Buyer("Barry Weiss", randomIncrement(10, 30), 800),
                new Buyer("Jarrod Schulz", randomIncrement(10, 30), 3000),
                new Buyer("Darrell Sheets", randomIncrement(10, 30), 5000),
                new Buyer("Dave Hester", randomIncrement(10, 30), 5000)
            )).start()
    }
}
