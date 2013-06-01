package org.fmi.dai.simulator

import scala.actors.Actor
import java.util.Date

trait Person extends Actor {
    def name(): String
    def attendAuction(auction: AuctionEngine)

    def log(msg: String) = { Console.println(name + ": " + msg) }
}

trait PersonAction
case class Offer(bid: Int, buyer: Person) extends PersonAction // a person makes a bid
case class InquireBid(buyer: Person) extends PersonAction // a person request the status of the auction

trait AuctionReply
case class Status(maxBid: Int, expiration: Date) extends AuctionReply // auction replies the status, the max bid and when it expires 
case class BeatenOffer(maxBid: Int) extends AuctionReply // auction replies with the best offer
case class AuctionConcluded(seller: Person, buyer: Person, bid: Int) extends AuctionReply // auction is over
case object BestOffer extends AuctionReply // yours is the best offer 
case object AuctionFailed extends AuctionReply // failed with no bids
case object AuctionOver extends AuctionReply // bidding is closed

