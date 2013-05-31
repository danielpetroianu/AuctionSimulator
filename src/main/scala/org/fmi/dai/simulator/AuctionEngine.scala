package org.fmi.dai.simulator

import java.util.Date
import scala.actors.Actor
import scala.actors.TIMEOUT
import org.fmi.dai.config.AuctionConfig

class AuctionEngine(minBid: Int, closing: Date, seller: Person, listOfBuyers: List[Person]) extends Actor {
    val _bidIncrement = 10

    def act() {
        var maxBid = minBid - _bidIncrement
        var topBuyer: Person = null

        loop {
            reactWithin(positiveOrZero(closing.getTime() - new Date().getTime())) {

                case Offer(bid, buyer) =>
                    if (bid >= maxBid + _bidIncrement) {
                        if (maxBid >= minBid) topBuyer ! BeatenOffer(bid)
                        maxBid = bid
                        topBuyer = buyer
                        topBuyer ! BestOffer
                    }
                    else {
                        buyer ! BeatenOffer(maxBid)
                    }

                case Inquire(buyer) =>
                    buyer ! Status(maxBid, closing)

                case TIMEOUT =>
                    if (maxBid >= minBid) {
                        val reply = AuctionConcluded(seller, topBuyer, maxBid)
                        topBuyer ! reply
                        seller ! reply
                    }
                    else {
                        seller ! AuctionFailed
                    }
                    reactWithin(AuctionConfig.AUCTION_HOUSE_TIME_TO_SHUTDOWN) {
                        case Offer(_, buyer) => buyer ! AuctionOver
                        case TIMEOUT         => exit()
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

    def positiveOrZero(number: Long): Long = if (number > 0) number else 0
}