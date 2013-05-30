package org.fmi.dai.simulator

import scala.actors.Actor
import scala.actors.TIMEOUT

class Buyer(name: String, increment: Int, top: Int, auctionHouse: AuctionHouse) extends Actor {
    import App._;

    val random = new scala.util.Random

    var max: Int = _
    var current: Int = 0

    def act() {

        logger.info("started")
        auctionHouse ! Inquire(this)
        receive {
            case Status(maxBid, _) =>
                logger.info("status(" + maxBid + ")")
                max = maxBid
        }

        loop {
            if (max >= top) {
                logger.info("too high for me")
            }
            else if (current < max) {
                current = max + increment
                Thread.sleep(1 + random.nextInt(1000))
                auctionHouse ! Offer(current, this)
            }

            reactWithin(3000) {
                case BestOffer =>
                    logger.info("bestOffer(" + current + ")")

                case BeatenOffer(maxBid) =>
                    logger.info("beatenOffer(" + maxBid + ")")
                    max = maxBid

                case AuctionConcluded(seller, maxBidder) =>
                    logger.info("auctionConcluded"); exit()

                case AuctionOver =>
                    logger.info("auctionOver"); exit()

                case TIMEOUT =>
                    exit()
            }
        }

    }

}