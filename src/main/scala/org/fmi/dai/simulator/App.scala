package org.fmi.dai.simulator

import org.fmi.dai.config.ZeroLoggerFactory
import scala.actors.Actor
import java.util.Date
import org.fmi.dai.config.AuctionConfig

object App {

    def main(args: Array[String]) {
        val startBid = 100
        val closing = new Date(new Date().getTime() + AuctionConfig.AUCTION_TIME)

        new AuctionEngine(startBid, closing,
            new Seller("John Doe"),
            List(
                new Buyer("Barry Weiss", 20, 800),
                new Buyer("Jarrod Schulz", 30, 3000),
                new Buyer("Darrell Sheets", 40, 5000),
                new Buyer("Dave Hester", 40, 5000)
            )).start()

    }
}
