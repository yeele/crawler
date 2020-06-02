//#full-example
package com.yeele


import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.yeele.GreeterMain.SayHello

//#greeter-actor
object Greeter {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    //#greeter-send-messages
    message.replyTo ! Greeted(message.whom, context.self)
    //#greeter-send-messages
    Behaviors.same
  }
}
//#greeter-actor

//#greeter-bot
object GreeterBot {

  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info("Greeting {} for {}", n, message.whom)
      if (n == max) {
        Behaviors.stopped
      } else {
        message.from ! Greeter.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
}
//#greeter-bot

//#greeter-main
object GreeterMain {

  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      //#create-actors
      val greeter = context.spawn(Greeter(), "greeter")
      //#create-actors

      Behaviors.receiveMessage { message =>
        //#create-actors
        val replyTo = context.spawn(GreeterBot(max = 3), message.name)
        //#create-actors
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
}
//#greeter-main

object MisoSoupMain {
  final case class MisoSoup(miso: String)

  def apply(): Behavior[MisoSoup] =
    Behaviors.setup { context =>
      //#create-actors
      val greeter = context.spawn(Greeter(), "greeter")
      //#create-actors

      Behaviors.receiveMessage { message =>
        context.log.info("my MisoSoup miso is {}", message.miso)
        Behaviors.same
      }
    }
}

//#main-class
object Crawler extends App {
//  //#actor-system
//  val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "akkaQuickStart")
//  //#actor-system
//
//  //#main-send-messages
//  greeterMain ! SayHello("Charles")
//  //#main-send-messages

  val gurdianActor: ActorSystem[MisoSoupMain.MisoSoup] = ActorSystem(MisoSoupMain(), "CrawlerGurdianActor")
  gurdianActor ! MisoSoupMain.MisoSoup("aka")
}
//#main-class
//#full-example
