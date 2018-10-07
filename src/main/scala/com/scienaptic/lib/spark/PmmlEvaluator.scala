package com.scienaptic.lib.spark

import java.io.FileInputStream

import mist.api.dsl.{arg, withArgs, _}
import mist.api.encoding.defaults._
import mist.api.encoding.spark.DataFrameEncoding._
import mist.api.{Handle, Logging, MistExtras, MistFn}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.jpmml.evaluator.spark.{EvaluatorUtil, TransformerBuilder}

import scala.tools.nsc.interpreter.InputStream

object PmmlEvaluator extends MistFn with Logging {

  private def readCsv(sparkSession: SparkSession, filePath: String): DataFrame =
    sparkSession.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(filePath)

  def handle: Handle =
    withArgs(
      arg[String]("csvFilePath"),
      arg[String]("pmmlFilePath")
    ).withMistExtras
      .onSparkSession(
        (csvFilePath: String,
         pmmlFilePath: String,
         extras: MistExtras,
         sparkSession: SparkSession) => {

          import extras._
          logger.info(s"Job Id - $jobId")

          val is: InputStream = new FileInputStream(pmmlFilePath)
          val evaluator = EvaluatorUtil.createEvaluator(is)

          val pmmlTransformerBuilder = new TransformerBuilder(evaluator)
            .withTargetCols()
            .withOutputCols()
            .exploded(true)

          val pmmlTransformer = pmmlTransformerBuilder.build()

          val input = readCsv(sparkSession, csvFilePath)
          val output = pmmlTransformer.transform(input)

          logger.info(input.columns.mkString(","))
          logger.info(output.columns.mkString(","))

          output

        }).asHandle
}
