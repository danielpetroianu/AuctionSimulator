package org.fmi.dai.simulator

import scala.actors.Actor
import java.util.Date
import org.fmi.dai.config.AuctionConfig

object App {
    val random = new scala.util.Random

    def rand(min: Int, max: Int): Int = min + random.nextInt(max - min)

    def main(args: Array[String]) {
        new AuctionEngine(
            AuctionConfig.AUCTION_START_BID,

            new Date(new Date().getTime() + AuctionConfig.AUCTION_DURATION),

            new Seller("John Doe"),

            List(
                new Buyer("Barry Weiss", rand(10, 30), 800),
                new Buyer("Jarrod Schulz", rand(10, 30), 3000),
                new Buyer("Darrell Sheets", rand(10, 30), 5000),
                new Buyer("Dave Hester", rand(10, 30), 5000)
            )
        ).start()
    }
}
