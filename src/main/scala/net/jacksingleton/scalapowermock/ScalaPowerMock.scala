package net.jacksingleton.scalapowermock

import com.thoughtworks.xstream.converters.ConversionException
import java.util.concurrent.Callable
import org.powermock.api.mockito.PowerMockito
import org.powermock.classloading.ClassloaderExecutor
import org.powermock.core.classloader.MockClassLoader
import org.powermock.core.MockRepository
import org.powermock.core.transformers.impl.MainMockTransformer
import org.powermock.core.transformers.MockTransformer
import org.powermock.reflect.proxyframework.RegisterProxyFramework
import org.powermock.reflect.Whitebox
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl
import scala.collection.JavaConverters._

/**
 * ScalaPowerMock
 *
 * Wrapper for PowerMock that allows convenient mocking of Scala objects
 */

case class ScalaPowerMockException(message: String, t: Throwable) extends Exception(message, t)

object ScalaPowerMock extends ScalaPowerMock

trait ScalaPowerMock {

  def withPowerMock[T](scalaObjects: Any*)(f: => T): T = {
    val mockLoader = new MockClassLoader(Array.empty, Array.empty)
    mockLoader.setMockTransformerChain(List[MockTransformer](new MainMockTransformer()).asJava)
    mockLoader.addClassesToModify(scalaObjects.map(_.getClass.getName): _*)
    mockLoader.addIgnorePackage("org.scalatest.*", "org.specs2.*", "scala.*", "java.*", "com.sun.*", "javax.*")

    val proxyFrameworkClass = Class.forName("org.powermock.api.extension.proxyframework.ProxyFrameworkImpl", false, mockLoader)
    val proxyFrameworkRegistrar = Class.forName(classOf[RegisterProxyFramework].getName, false, mockLoader)
    Whitebox.invokeMethod(proxyFrameworkRegistrar, "registerProxyFramework", Whitebox.newInstance(proxyFrameworkClass).asInstanceOf[Object])

    new MockPolicyInitializerImpl((f _).getClass).initialize(mockLoader)

    val setInternalState = classOf[Whitebox].newInstance.asInstanceOf[{def setInternalState(o: Object, field: String, value: Object)}].setInternalState _

    val mockObjectsAndRun = new Callable[T] {
      def call(): T = {
        try {
          scalaObjects.foreach { scalaObject =>
            PowerMockito.mockStatic(scalaObject.getClass)
            val mockScalaObject = PowerMockito.mock(scalaObject.getClass)
            setInternalState(scalaObject.getClass, "MODULE$", mockScalaObject.asInstanceOf[Object])
          }
          f
        } finally {
          MockRepository.clear()
        }
      }
    }

    try {
      new ClassloaderExecutor(mockLoader).execute(mockObjectsAndRun)
    } catch {
      case ce: ConversionException => throw ScalaPowerMockException("Object graph may be too complex to clone into new classloader, could be caused by a fancy test framework. Try invoking 'withPowerMock' from outside the context of your test", ce)
    }
  }
}
