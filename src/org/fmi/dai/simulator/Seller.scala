package org.fmi.dai.simulator

import scala.actors.Actor
import scala.actors.TIMEOUT

class Seller(name: String) extends Person {
    var _auctionHouse: AuctionEngine = null

    def act() {

        log("joined the Auction as a Seller.")

        receive {
            case AuctionConcluded(_, topBidder, bid) =>
                log(":) Sold my item to " + topBidder.name + " for " + bid + ".")
                exit()

            case AuctionFailed =>
                log("I'll sell it next time... :( ")
                exit()
        }
    }

    def attendAuction(auction: AuctionEngine) {
        _auctionHouse = auction;
    }

    def name(): String = {
        name
    }
}