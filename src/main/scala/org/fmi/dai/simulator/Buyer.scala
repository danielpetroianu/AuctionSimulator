package org.fmi.dai.simulator

import scala.actors.Actor
import scala.actors.TIMEOUT
import org.fmi.dai.config.AuctionConfig

class Buyer(name: String, increment: Int, top: Int) extends Person {
    import App._;

    val random = new scala.util.Random
    var _maxBid: Int = _
    var _current: Int = 0
    var _auctionHouse: AuctionEngine = null

    def act() {

        log("joined the Auction as a Buyer.")

        _auctionHouse ! Inquire(this)
        receive {
            case Status(maxBid, _) =>
                log("status(" + maxBid + ")")
                _maxBid = maxBid
        }

        loop {
            if (_maxBid >= top) {
                log("I'm not going to pay that much for it. I'm out.")
            }
            else if (_current < _maxBid) {
                _current = _maxBid + increment
                Thread.sleep(1 + random.nextInt(1000))
                _auctionHouse ! Offer(_current, this)
            }

            reactWithin(AuctionConfig.AUCTION_HOUSE_TIME_TO_SHUTDOWN) {
                case BestOffer =>
                    log("( i have the best offer - " + _current + ")")

                case BeatenOffer(maxBid) =>
                    log("( my offer was beaten - new bid is " + maxBid + ")")
                    _maxBid = maxBid

                case AuctionConcluded(seller, maxBidder, bid) =>
                    log("Yes!! I'm taking this item home for " + bid + ".")
                    exit()

                case AuctionOver =>
                    log("That item simply wasn't good enough for my money.")
                    exit()

                case TIMEOUT =>
                    exit()
            }
        }

    }

    def attendAuction(auction: AuctionEngine) {
        _auctionHouse = auction;
    }

    def name(): String = {
        name
    }

}