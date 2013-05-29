package org.fmi.dai.simulator

import java.util.Date
import scala.actors.Actor
import scala.actors.TIMEOUT

class AuctionHouse(seller: Actor, minBid: Int, closing: Date) extends Actor {
    val timeToShutdown = 3000 // msec
    val bidIncrement = 10

    def act() {
        var maxBid = minBid - bidIncrement
        var maxBidder: Actor = null

        loop {
            reactWithin(closing.getTime() - new Date().getTime()) {

                case Offer(bid, client) =>
                    if (bid >= maxBid + bidIncrement) {
                        if (maxBid >= minBid) maxBidder ! BeatenOffer(bid)
                        maxBid = bid
                        maxBidder = client
                        client ! BestOffer
                    }
                    else {
                        client ! BeatenOffer(maxBid)
                    }

                case Inquire(client) =>
                    client ! Status(maxBid, closing)

                case TIMEOUT =>
                    if (maxBid >= minBid) {
                        val reply = AuctionConcluded(seller, maxBidder)
                        maxBidder ! reply
                        seller ! reply
                    }
                    else {
                        seller ! AuctionFailed
                    }
                    reactWithin(timeToShutdown) {
                        case Offer(_, client) => client ! AuctionOver
                        case TIMEOUT          => exit()
                    }

            }
        }
    }
}