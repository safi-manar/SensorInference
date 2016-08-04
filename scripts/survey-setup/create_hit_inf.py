#!/usr/bin/env python3

import mturk

question = '''<?xml version="1.0"?>
<QuestionForm xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionForm.xsd">
    <Overview>
        <FormattedContent><![CDATA[
            <p><img alt="App logo" src="https://blues.cs.berkeley.edu/sensor-inference/images/app-logo.png" /> UC Berkeley Sensor Study</p>

            <p>Project site: <a href = "https://blues.cs.berkeley.edu/sensor-inference/">https://blues.cs.berkeley.edu/sensor-inference/</a></p>

            <p>This is a week-long data collection task that&#39;s part of a study to identify relationships between user information and smartphone sensor measurements. Participants will install an app on their Android smartphone and respond to periodic surveys over the course of the week.</p>

            <p>Payments are made upon completion of surveys:</p>

            <ul>
            <li>$1.00 for the entry demographic survey</li>
            <li>$1.00 bonus for an optional daily activity survey (presented at 8:00 PM each night, max 6 nights)</li>
            <li>$2.00 bonus for an optional exit survey</li>
            </ul>

            <p>Our app requires the following permissions:</p>

            <ul>
            <li>Phone calls, in order to detect a Wi-Fi connection and reduce cellular data usage; <em>we do not make any phone calls at all</em></li>
            <li>Location, in order to establish a reference (along with survey responses); <em>GPS data will be kept confidential and never shared with anyone</em></li>
            </ul>

            <p>&#160;</p>

            <p><u>How to install the app and participate in the study:</u></p>

            <ol>
            <li>From a compatible Android phone, point your browser to <strong><em>http://tinyurl.com/ucbsensors</em></strong> (this redirects to https://blues.cs.berkeley.edu/sensor-inference/app.html, which will open our app&#39;s Google Play Store listing)</li>
            <li>Install our Sensor Monitor app through the Google Play Store. Unfortunately, not all phones meet our study's needs.</li>
            <li>Review our consent form, grant the appropriate permissions, and complete the entry survey</li>
            <li>Enter your verification code in the box below -- <strong><em>Accurate entry</em></strong><em><strong> is necessary to ensure assignment approvals and bonus payments.</strong></em></li>
            <li>Optional: Complete daily surveys for bonus payments</li>
            <li>The app will prompt you to uninstall it at the end of the study period</li>
            </ol>
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
