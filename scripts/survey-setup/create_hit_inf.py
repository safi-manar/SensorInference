#!/usr/bin/env python3

import mturk

question = '''<?xml version="1.0"?>
<QuestionForm xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionForm.xsd">
    <Overview>
        <FormattedContent><![CDATA[
            <!-- For help on using this template, see the blog post: http://mechanicalturk.typepad.com/blog/2014/04/editing-the-survey-link-project-template-in-the-ui.html --><!-- Bootstrap v3.0.3 -->
            <p><strong><em>THIS IS PART OF A WEEK-LONG STUDY TO EXPLORE INFERRING USER INFORMATION FROM SMARTPHONE SENSORS LIKE ACCELEROMETERS, STEP COUNTERS, AND LIGHT DETECTORS. PARTICIPANTS WILL NEED TO INSTALL AN ANDROID APP THAT PERIODICALLY COLLECTS SENSOR DATA AND PRESENTS SURVEYS.</em></strong></p>

            <p>Payments are made upon completion of surveys:</p>

            <ul>
                <li>$1.00 for the entry demographic survey</li>
                <li>$1.00 bonus for an optional daily activity survey (presented at 8:00 PM each night, max 6 nights)</li>
                <li>$2.00 bonus for an optional exit survey</li>
            </ul>

            <p>&#160;</p>

            <p>Participants need to enter the app-generated code upon completion of the entry survey. <em><strong>This is necessary to ensure payment. Bonus payments depend on this, so please do not generate a new one by reinstalling the app.</strong></em></p>

            <p align="center"><img src="http://i.imgur.com/zSs896i.png" alt="App code example"/></p>

            <p>&#160;</p>

            <p>The Android app requires the following permissions:</p>

            <ul>
                <li>Phone calls, in order to detect a Wi-Fi connection and reduce cellular data usage; <em>we do not make any phone calls at all</em></li>
                <li>Location, in order to establish a reference (along with survey responses); <em>GPS data will be kept confidential and never shared with anyone</em></li>
            </ul>

            <p align="center"><img alt="http://i.imgur.com/AdkMHZz.jpg" src="http://i.imgur.com/AdkMHZz.jpg" /><img alt="http://i.imgur.com/V1LfjDR.jpg" src="http://i.imgur.com/V1LfjDR.jpg" /></p>

            <p>&#160;</p>

            <table>
                <tbody>
                    <tr>
                        <td><strong>Google Play Store link (requires Android 5.0 or higher):</strong></td>
                        <td><a href="https://play.google.com/apps/testing/edu.berkeley.icsi.sensormonitor" target="_blank">Search for icsi sensormonitor on the Play Store on your phone (https://play.google.com/apps/testing/edu.berkeley.icsi.sensormonitor)</a></td>
                    </tr>
                </tbody>
            </table>
            <!-- close container section -->
        ]]></FormattedContent>
    </Overview>
    <Question>
        <QuestionIdentifier>surveycode</QuestionIdentifier>
        <DisplayName>App Code</DisplayName>
        <IsRequired>true</IsRequired>

        <QuestionContent><Text>Enter your MTURK verification code here:</Text></QuestionContent>
        <AnswerSpecification>
            <FreeTextAnswer>
                <Constraints><Length minLength="8" maxLength="8" /></Constraints>
                <NumberOfLinesSuggestion>1</NumberOfLinesSuggestion>
            </FreeTextAnswer>
        </AnswerSpecification>
    </Question>
</QuestionForm>
'''

options = {
    'Title': 'UC Berkeley Sensor Inference Study',
    'Description': 'Download our Android app, fill out a survey, and let the app run for a week ( + $1.00 bonus per daily survey) .',
    'Keywords': 'Android, app, download, install, survey, bonus',

    'Reward': [{
        'Amount': 1.00,
        'CurrencyCode': 'USD'
    }],
    'MaxAssignments': 200,
    'AssignmentDurationInSeconds': 60*60,
    'LifetimeInSeconds': 60*60*24*7,
    'AutoApprovalDelayInSeconds' : 60*60*24*10,

    'QualificationRequirement': [{
        'QualificationTypeId': mturk.LOCALE,
        'Comparator': 'EqualTo',
        'LocaleValue': {
            'Country': 'US'
        },
        'RequiredToPreview': 'true'
    }, {
#       'QualificationTypeId': mturk.P_APPROVED,
#       'Comparator': 'GreaterThan',
#       'IntegerValue': 95,
#       'RequiredToPreview': 'true'
#   }, {
        'QualificationTypeId': mturk.ADULT,
        'Comparator': 'EqualTo',
        'IntegerValue': 1,
        'RequiredToPreview': 'true'
    }],

    'Question': question
}

m = mturk.MechanicalTurk()
r = m.request('CreateHIT', options)
print(r)
