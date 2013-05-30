package org.fmi.dai.simulator

import java.util.Date
import scala.actors.Actor
import scala.actors.TIMEOUT
import org.fmi.dai.config.AuctionConfig

class AuctionHouse(seller: Actor, minBid: Int, closing: Date) extends Actor {

    val bidIncrement = 10

    def act() {
        var maxBid = minBid - bidIncrement
        var maxBidder: Actor = null

        loop {
            reactWithin(closing.getTime() - new Date().getTime()) {

                case Offer(bid, buyer) =>
                    if (bid >= maxBid + bidIncrement) {
                        if (maxBid >= minBid) maxBidder ! BeatenOffer(bid)
                        maxBid = bid
                        maxBidder = buyer
                        buyer ! BestOffer
                    }
                    else {
                        buyer ! BeatenOffer(maxBid)
                    }

                case Inquire(buyer) =>
                    buyer ! Status(maxBid, closing)

                case TIMEOUT =>
                    if (maxBid >= minBid) {
                        val reply = AuctionConcluded(seller, maxBidder)
                        maxBidder ! reply
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
}