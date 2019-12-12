package com.example.clapsdetector.util

import kotlin.math.*

class TComplex {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Tests for complex numbers class:")
            val first = TComplex(1.0, 1.0)
            println(first)
            val second = TComplex(2.0, -3.0)
            println(second)
            println("Sum: ${first + second}")
            println("Difference: ${first - second}")
            println("Multiply: ${first * second}")
            println("Dividing: ${first / second}")

            println("Module: ${first.module()}")
            println("Argument: ${first.argument()}")
            println("Angle: ${first.angle()}")
            println("Square: ${first.square()}")

            println("Pow 3: ${first.pow(3)}")
            println("Root 3 3: ${first.root(3, 3)}")
            println("String parsing: ${TComplex.parse("-1 + -i*2")}")
        }

        fun parse(complexString: String): TComplex = TComplex(complexString)
    }

    var real = 0.0
    var image = 0.0

    constructor(real: Double, image: Double) {
        this.real = real
        this.image = image
    }

    constructor(complexString: String) {
        parse(complexString)
    }

    private fun init(real: Double, image: Double) {
        this.real = real
        this.image = image
    }


    fun copy(): TComplex = TComplex(this.real, this.image)

    operator fun plus(addition: TComplex): TComplex =
        TComplex(
            this.real + addition.real,
            this.image + addition.image
        )

    operator fun minus(subtrahend: TComplex): TComplex =
        TComplex(
            this.real - subtrahend.real,
            this.image - subtrahend.image
        )

    operator fun times(multiplier: TComplex): TComplex =
        TComplex(
            this.real * multiplier.real - this.image * multiplier.image,
            this.real * multiplier.image + this.image * multiplier.real
        )

    operator fun div(divider: TComplex): TComplex =
        TComplex(
            (this.real * divider.real + this.image * divider.image) / divider.real * divider.real + divider.image * divider.image,
            (this.image * divider.real + this.real * divider.image) / divider.real * divider.real + divider.image * divider.image
        )

    fun square(): TComplex =
        TComplex(this.real * this.real - this.image * this.image, 2.0 * this.real * this.image)

    fun pow(degree: Int): TComplex {
        val module = this.module().pow(degree)
        val argument = this.argument()
        return TComplex(module * cos(degree * argument), sin(degree * argument))
    }

    fun argument(): Double {
        return if (this.real > 0.0) atan(this.image / this.real)
        else if (this.real < 0.0) atan(this.image / this.real) + Math.PI
        else if (this.image > 0.0) Math.PI / 2
        else -Math.PI / 2
    }

    fun angle(): Double {
        return argument() * 180.0 / Math.PI
    }

    fun module(): Double {
        return sqrt(this.real * this.real + this.image * this.image)
    }

    fun root(degree: Int, rootNumber: Int): TComplex {
        val k = rootNumber.rem(degree)
        val module = this.module().pow(1.0 / degree)
        val argument = this.argument()
        return TComplex(
            module * cos((argument + 2.0 * PI * k) / degree),
            module * sin((argument + 2.0 * PI * k) / degree)
        )
    }

    override fun equals(other: Any?): Boolean {
        return (other is TComplex) && (this.image == other.image && this.real == other.real)
    }

    fun getRealString(): String = this.real.toString()

    fun getImageString(): String = this.image.toString()

    override fun toString(): String {
        //val sign = if (image < 0.0) "-" else "+"
        return String.format("%.5g + i*%.5g", real, image)
    }

    private fun parse(complexString: String) {
        this.real = 0.0
        this.image = 0.0
        val parts = complexString.apply { replace(" ", "") }.split("+")
        try {
            if (parts.size > 2) {
                throw WrongNumberException("Wrong complex number notation")
            } else if (parts.size == 2) {
                var imageIndex = 1
                var realIndex = 0
                if (!parts[0].contains("i") && !parts[1].contains("i")) {
                    throw WrongNumberException("Wrong complex number notation")
                } else if (parts[0].contains("i")) {
                    imageIndex = 0
                    realIndex = 1
                }
                parts[imageIndex].let {
                    this.image = it
                        .replace("*", "")
                        .replace("i", "")
                        .toDouble()
                }
                this.real = parts[realIndex].toDouble()
            } else if (parts.size == 1) {
                if (parts.contains("i")) {
                    parts[0].let {
                        this.image = it
                            .replace("*", "")
                            .replace("i", "")
                            .toDouble()
                    }
                } else {
                    this.real = parts[0].toDouble()
                }
            }
        } catch (ex: Exception) {
            throw WrongNumberException(ex.toString())
        }
    }

    class WrongNumberException(var description: String) : Exception() {
        override fun toString(): String {
            return "WrongNumberException: ${description}"
        }
    }
}