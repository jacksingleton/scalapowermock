import net.jacksingleton.scalapowermock.ScalaPowerMock._
import net.jacksingleton.scalapowermock.ScalaPowerMockException
import org.mockito.Mockito
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.matchers.ShouldMatchers._


object ScalaPowerMockWithScalaTestSpec {

  object SomeScalaObject {
    def doSomething = "object does something"
  }

  def callDoSomething() {
    withPowerMock(SomeScalaObject) {
      val mockedValue: String = "mocked"
      Mockito.when(SomeScalaObject.doSomething).thenReturn(mockedValue)

      SomeScalaObject.doSomething should be (mockedValue)
    }
  }
}

class ScalaPowerMockWithScalaTestSpec extends FlatSpec with ShouldMatchers {
  import ScalaPowerMockWithScalaTestSpec._

  "withPowerMock inside ScalaTest" should "work when called from outside the test" in {
    callDoSomething()
  }

  // TODO: Fix me
  it should "fail gracefully when called from inside ScalaTest" in {
    intercept[ScalaPowerMockException] {
      withPowerMock(SomeScalaObject) {
        val mockedValue: String = "mocked"
        Mockito.when(SomeScalaObject.doSomething).thenReturn(mockedValue)

        SomeScalaObject.doSomething should be (mockedValue)
      }
    }
  }

}
