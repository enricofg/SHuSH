import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When



class at10 {

	@Given("I have clicked the share button")
	def I_click_share_button() {
		Mobile.startExistingApplication("com.example.shush")
		sleep(3000)
		Mobile.tap(findTestObject('BtnStart'), 0)
		sleep(3000)
		Mobile.tap(findTestObject('BtnShare'), 0)
	}

	@Given("the confirmation window asks me if I want to share the information")
	def confirm_share_info_window() {
		Mobile.verifyElementExist(findTestObject('BtnShareYes'), 0)
	}

	@Given("the Ruído atual label has a value")
	def ruido_atual_value() {
		String value =Mobile.getAttribute(findTestObject('TextSendData'),'text', 0)
		String[] valsPar = value.split('\n')
		Mobile.verifyNotEqual(valsPar[1][13], "");
	}
	@Given("the Data label has a value")
	def data_atual_value() {
		String value =Mobile.getAttribute(findTestObject('TextSendData'),'text', 0)
		String[] valsPar = value.split('\n')
		Mobile.verifyNotEqual(valsPar[2][6], "");
	}
	@Given("the Localização label has a value")
	def localizacao_atual_atual_value() {
		String value =Mobile.getAttribute(findTestObject('TextSendData'),'text', 0)
		String[] valsPar = value.split('\n')
		Mobile.verifyNotEqual(valsPar[3][13], "");
	}
	@Then("I click Sim")
	def i_click_sim_share(){
		Mobile.tap(findTestObject('BtnShareYes'), 0)
	}
	@Then("I click Não")
	def i_click_nao_share(){
		Mobile.tap(findTestObject('BtnShareNo'), 0)
	}
	@Then("I verify that I cannot click the Sim button to share the same information again")
	def i_verify_share_disabled(){
		Mobile.verifyElementNotExist(findTestObject('BtnShareYes'), 0)
	}
}