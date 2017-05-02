package minesweeper.neural

import org.jblas.{DoubleMatrix, SimpleBlas}

class Layer(var lastInput: DoubleMatrix,
            var weights: DoubleMatrix,
            var fixedWeights: DoubleMatrix,
            var lastOutput: DoubleMatrix) {

    def forward(input: DoubleMatrix): DoubleMatrix = {
        lastInput = input
        val multiplied = weights.mmul(input)
        val output = tanh(multiplied.add(fixedWeights))
        lastOutput = output
        output
    }

    def learn(stepFactor: Double, gradient: DoubleMatrix): DoubleMatrix = {
        val factor = dtanh(lastOutput).mul(gradient)
        val Gw = factor.mmul(lastInput.transpose())

        //W = stepFactor*Gw + W
        SimpleBlas.axpy(stepFactor, Gw, weights)
        SimpleBlas.axpy(stepFactor, factor, fixedWeights)

        weights.transpose().mmul(factor)
    }

    private def tanh(x: Double): Double = {
        val ex = Math.exp(x)
        val eMinusx = 1.0d / ex
        (ex - eMinusx) / (ex + eMinusx)
    }

    /**
      * Happens inplace
      */
    private def tanh(x: DoubleMatrix): DoubleMatrix = {
        x.data = x.data.map(tanh)
        x
    }


    private def dtanh(x: DoubleMatrix): DoubleMatrix = {
        //1 - X*X = -X*X + 1
        x.mul(x).neg().add(1)
    }
}

