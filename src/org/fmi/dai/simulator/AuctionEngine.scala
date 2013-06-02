package org.fmi.dai.simulator

import java.util.Date
import scala.actors.Actor
import scala.actors.TIMEOUT
import org.fmi.dai.config.AuctionConfig

class AuctionEngine(startBid: Int, duration: Date, seller: Person, listOfBuyers: List[Person]) extends Actor {

    def act() {
        var maxBid = startBid - AuctionConfig.AUCTION_BID_INCREMENT_THRESHOLD
        var topBuyer: Person = null

        loop {
            reactWithin(positiveOrZero(duration.getTime() - new Date().getTime())) {

                case InquireBid(buyer) =>
                    buyer ! Status(maxBid, duration)

                case Offer(bid, buyer) =>
                    if (bid >= maxBid + AuctionConfig.AUCTION_BID_INCREMENT_THRESHOLD) {
                        if (maxBid >= startBid) topBuyer ! BeatenOffer(bid)
                        maxBid = bid
                        topBuyer = buyer
                        topBuyer ! BestOffer
                    }
                    else {
                        buyer ! BeatenOffer(maxBid)
                    }

                case TIMEOUT =>
                    if (maxBid >= startBid) {
                        val reply = AuctionConcluded(seller, topBuyer, maxBid)
                        topBuyer ! reply
                        seller ! reply
                    }
                    else {
                        seller ! AuctionFailed
                    }

                    reactWithin(AuctionConfig.AUCTION_CLOSING_DELAY) {
                        case Offer(_, buyer) =>
                            log("Sorry " + buyer.name + ", the auction is closed.")
                            buyer ! AuctionOver

                        case TIMEOUT => exit()
                    }

            }
        }
    }

    override def start(): Actor = synchronized {
        super.start()

        seller.attendAuction(this)
        seller.start()

        listOfBuyers.foreach { buyer =>
            buyer.attendAuction(this)
            buyer.start()
        }

        this
    }

    def positiveOrZero(number: Long): Long = { if (number > 0) number else 0 }

    def log(msg: String) = { Console.println("AuctionHouse: " + msg) }
}