/**
  * Created by myousuff on 31.08.17.
  */
object FibonacciTest {

  def main(args: Array[String]): Unit = {

    val k : Long = 12345678 * 93247

    println(primeFactor(k))

  }
  var  m = 2
  var newN: Long =0
  var t = List[Long]()

  def primeFactor(n: Long): List[Long] = {

    if (n < 2)  t  else {
        if ( n % m == 0) {
          t = m :: t
          newN = (n / m)
          if (newN > 1) {  m=2;primeFactor(newN) }
        }
        else {
          m = m+1
          primeFactor(n)
        }
        }

      t
    }
}