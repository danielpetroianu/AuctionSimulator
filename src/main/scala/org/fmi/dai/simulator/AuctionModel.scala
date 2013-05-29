package org.fmi.dai.simulator

import scala.actors.Actor
import java.util.Date

trait AuctionMessage
case class Offer(bid: Int, client: Actor) extends AuctionMessage // make a bid
case class Inquire(client: Actor) extends AuctionMessage // inquire status

trait AuctionReply
case class Status(asked: Int, expiration: Date) extends AuctionReply // asked sum, expiration date
case class BeatenOffer(maxBid: Int) extends AuctionReply // offer beaten by maxBid
case class AuctionConcluded(seller: Actor, client: Actor) extends AuctionReply // auction concluded
case object BestOffer extends AuctionReply // yours is the best offer 
case object AuctionFailed extends AuctionReply // failed with no bids
case object AuctionOver extends AuctionReply // bidding is closed
