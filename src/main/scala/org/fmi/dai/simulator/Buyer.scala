package org.fmi.dai.simulator

import scala.actors.Actor
import scala.actors.TIMEOUT

class Buyer(name: String, increment: Int, top: Int, auction: AuctionHouse) extends Actor {

    def log(msg: String) = Console.println(name + ": " + msg)

    val random = new scala.util.Random

    var max: Int = _
    var current: Int = 0

    def act() {

        log("started")
        auction ! Inquire(this)
        receive {
            case Status(maxBid, _) =>
                log("status(" + maxBid + ")")
                max = maxBid
        }

        loop {
            if (max >= top) {
                log("too high for me")
            }
            else if (current < max) {
                current = max + increment
                Thread.sleep(1 + random.nextInt(1000))
                auction ! Offer(current, this)
            }

            reactWithin(3000) {
                case BestOffer =>
                    log("bestOffer(" + current + ")")

                case BeatenOffer(maxBid) =>
                    log("beatenOffer(" + maxBid + ")")
                    max = maxBid

                case AuctionConcluded(seller, maxBidder) =>
                    log("auctionConcluded"); exit()

                case AuctionOver =>
                    log("auctionOver"); exit()

                case TIMEOUT =>
                    exit()
            }
        }

    }

}