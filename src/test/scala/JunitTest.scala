import net.jacksingleton.scalapowermock.ScalaPowerMock._
import org.junit.{Ignore, Test}
import org.junit.Assert._
import org.mockito.Mockito

object ScalaObject1 {
  def doSomething = "scala object 1 does something"
}

object ScalaObject2 {
  def doSomething = "scala object 2 does something"
}

object JunitTest {
  object NestedScalaObject {
    def doSomething = "nested scala object does something"
  }
}

class JunitTest {

  object PathDependantScalaObject {
    def doSomething = "path dependant scala object does something"
  }

  @Test
  def mock_scala_object {
    withPowerMock(ScalaObject1) {
      // Given
      val mockedValue = "mocked"
      Mockito.when(ScalaObject1.doSomething).thenReturn(mockedValue)

      // When / Then
      assertEquals(mockedValue, ScalaObject1.doSomething)
    }
  }

  @Test
  def mock_two_scala_objects {
    withPowerMock(ScalaObject1, ScalaObject2) {
      // Given
      val mockedValue1 = "mocked1"
      Mockito.when(ScalaObject1.doSomething).thenReturn(mockedValue1)
      val mockedValue2 = "mocked2"
      Mockito.when(ScalaObject2.doSomething).thenReturn(mockedValue2)

      // When / Then
      assertEquals(mockedValue1, ScalaObject1.doSomething)
      assertEquals(mockedValue2, ScalaObject2.doSomething)
    }
  }

  @Test
  def mock_nested_scala_object {
    import JunitTest._
    withPowerMock(NestedScalaObject) {
      // Given
      val mockedValue = "mocked"
      Mockito.when(NestedScalaObject.doSomething).thenReturn(mockedValue)

      // When / Then
      assertEquals(mockedValue, NestedScalaObject.doSomething)
    }
  }

  @Test
  @Ignore // TODO: Fix me
  def mock_path_dependant_scala_object {
    withPowerMock(PathDependantScalaObject) {
      // Given
      val mockedValue = "mocked"
      Mockito.when(PathDependantScalaObject.doSomething).thenReturn(mockedValue)

      // When / Then
      assertEquals(mockedValue, PathDependantScalaObject.doSomething)
    }
  }
}
