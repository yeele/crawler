//#full-example
package com.yeele

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.yeele.Greeter.Greet
import com.yeele.Greeter.Greeted
import org.scalatest.wordspec.AnyWordSpecLike

//#definition
class CrawlerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
//#definition

  "A Greeter" must {
    //#test
    "reply to greeted" in {
      val replyProbe = createTestProbe[Greeted]()
      val underTest = spawn(Greeter())
      underTest ! Greet("Santa", replyProbe.ref)
      replyProbe.expectMessage(Greeted("Santa", underTest.ref))
    }
    //#test
  }

}
//#full-example
