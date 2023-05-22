import matplotlib.pyplot as IHATEEVERYTHINGABOUTPYTHON
import numpy as WHYDONTYOUCALLITNPIMMEDIATELY
from scipy import stats
import csv

indexLastPage = 2
lastPageNumber = '36'
indexGroupID = 13
indexQuestion = [90,95,100,105,110,115,120,125,130,135,140,145,150,155,160]
indexOpinion = [161,162,163,164]
indexQuestionTime = [265,271,277,283,289,295,301,307,313,319,325,331,337,343,349]
correctAnswers = ['Yes','7','No','2','10','No','6','Yes','1','3','No','5','Yes','4','4']

questionCount=15
opinionCount=4
modelCount=4
numericQuestionIndices = [1,3,4,6,8,9,11,13,14]
numericOpinionIndices = [0,1]
question = []
correct = []
opinion = []
questionTime = []
for q in range(0,questionCount):
	qu = []
	qt = []
	ca = []
	for m in range(0,modelCount):
		qum = []
		qtm = []
		cam = []
		qu.append(qum)
		qt.append(qtm)
		ca.append(cam)
	question.append(qu)
	questionTime.append(qt)
	correct.append(ca)
for o in range(0,opinionCount):
	op = []
	for m in range(0,modelCount):
		opm = []
		op.append(opm)
	opinion.append(op)

with open('results.csv', newline='\n') as csvfile:
	resultsReader = csv.reader(csvfile, delimiter=',',quotechar='"')
	row1 = next(resultsReader)
	i = 0;
	for entry in resultsReader:
		# Valid Entry!
		if entry[indexLastPage] == lastPageNumber:
			groupID = int(entry[indexGroupID])-1
			for questionID in range(0,questionCount):
				questionEntry= entry[indexQuestion[questionID]]
				if questionEntry == correctAnswers[questionID]:
					correct[questionID][groupID].append(1)
				else:
					correct[questionID][groupID].append(0)
				if questionID in numericQuestionIndices:
					questionEntry = int(questionEntry)
				else:
					if questionEntry == 'Yes':
						questionEntry = 1
					else:
						questionEntry = 0
				question[questionID][groupID].append(questionEntry)
				questionTime[questionID][groupID].append(float(entry[indexQuestionTime[questionID]]))
			for opinionID in range(0,opinionCount):
				opinionEntry = entry[indexOpinion[opinionID]]
				#TODO: Add back once we fixed the entries!
				#if opinionID in numericOpinionIndices:
					 #opinionEntry = int(opinionEntry)
				if opinionID in numericOpinionIndices:
					opinionEntry = opinionEntry
				else:
					if opinionEntry == 'Yes' or opinionEntry == 'Too fast':
						opinionEntry = 1
					else:
						if opinionEntry == 'Too slow':
							opinionEntry = -1
						else:
							opinionEntry = 0
				opinion[opinionID][groupID].append(opinionEntry)
	for questionID in range(0,questionCount):
		print("Question " + str(questionID+1)+":\t" + str(question[questionID][0]) +"\t" + str(question[questionID][1]) + "\t" + str(question[questionID][2]) + "\t" + str(question[questionID][3]))
		plot,ax = IHATEEVERYTHINGABOUTPYTHON.subplots(figsize =(10,7))
		answerrange = range(min(min(question[questionID][0]),min(question[questionID][1]),min(question[questionID][2]),min(question[questionID][3])),max(max(question[questionID][0]),max(question[questionID][1]),max(question[questionID][2]),max(question[questionID][3]))+2)
		data = [question[questionID][0],question[questionID][1],question[questionID][2],question[questionID][3]]
		IHATEEVERYTHINGABOUTPYTHON.hist(data, bins=answerrange, range=0.1,density='true')
		ax.set_xticks(answerrange)
		IHATEEVERYTHINGABOUTPYTHON.title("Question " + str(questionID+1))
		IHATEEVERYTHINGABOUTPYTHON.savefig("q" + str(questionID+1) + ".png")
	
	print("\n")
	for questionID in range(0,questionCount):
		print("Correct " + str(questionID+1)+":\t" + str(correct[questionID][0]) +"\t" + str(correct[questionID][1]) + "\t" + str(correct[questionID][2]) + "\t" + str(correct[questionID][3]))
		plot,ax = IHATEEVERYTHINGABOUTPYTHON.subplots(figsize =(10,7))
		answerrange = range(0,3)
		data = [correct[questionID][0],correct[questionID][1],correct[questionID][2],correct[questionID][3]]
		IHATEEVERYTHINGABOUTPYTHON.hist(data, bins=answerrange, range=0.1,density='true')
		ax.set_xticks(answerrange)
		IHATEEVERYTHINGABOUTPYTHON.title("Correct Answers " + str(questionID+1))
		IHATEEVERYTHINGABOUTPYTHON.savefig("c" + str(questionID+1) + ".png")
		#if questionID in numericQuestionIndices:
		#	plot = IHATEEVERYTHINGABOUTPYTHON.figure(figsize =(10,7))
		#	data = [question[questionID][0],question[questionID][1],question[questionID][2],question[questionID][3]]
		#	IHATEEVERYTHINGABOUTPYTHON.boxplot(data)
		#	IHATEEVERYTHINGABOUTPYTHON.title("Question " + str(questionID+1))
		#	IHATEEVERYTHINGABOUTPYTHON.show()
		
	print("\n")
	for questionID in range(0,questionCount):
		print("Question Time " + str(questionID+1)+":\t" + str(questionTime[questionID][0]) +"\t" + str(questionTime[questionID][1]) + "\t" + str(questionTime[questionID][2]) + "\t" + str(questionTime[questionID][3]))
		plot = IHATEEVERYTHINGABOUTPYTHON.figure(figsize =(10,7))
		data = [questionTime[questionID][0],questionTime[questionID][1],questionTime[questionID][2],questionTime[questionID][3]]
		IHATEEVERYTHINGABOUTPYTHON.boxplot(data)
		IHATEEVERYTHINGABOUTPYTHON.title("Question Time " + str(questionID+1))
		IHATEEVERYTHINGABOUTPYTHON.savefig("qt" + str(questionID+1) + ".png")
		
	print("\n")
	for opinionID in range(0,opinionCount):
		print("Opinion " + str(opinionID+1)+":\t" + str(opinion[opinionID][0]) +"\t" + str(opinion[opinionID][1]) + "\t" + str(opinion[opinionID][2]) + "\t" + str(opinion[opinionID][3]))
		if opinionID in range(2,4):
			plot,ax = IHATEEVERYTHINGABOUTPYTHON.subplots(figsize =(10,7))
			answerrange = range(min(min(opinion[opinionID][0]),min(opinion[opinionID][1]),min(opinion[opinionID][2]),min(opinion[opinionID][3])),max(max(opinion[opinionID][0]),max(opinion[opinionID][1]),max(opinion[opinionID][2]),max(opinion[opinionID][3]))+2)
			data = [opinion[opinionID][0],opinion[opinionID][1],opinion[opinionID][2],opinion[opinionID][3]]
			IHATEEVERYTHINGABOUTPYTHON.hist(data, bins=answerrange, range=0.1,density='true')
			ax.set_xticks(answerrange)
			IHATEEVERYTHINGABOUTPYTHON.title("Opinion " + str(opinionID+1))
		IHATEEVERYTHINGABOUTPYTHON.savefig("o" + str(opinionID+1) + ".png")
		
	for questionID in range(0,questionCount):
		print("Question " + str(questionID+1))
		if questionID != 0:
			print(stats.kruskal(question[questionID][0],question[questionID][1],question[questionID][2],question[questionID][3]))
			
	for questionID in range(0,questionCount):
		print("Correct " + str(questionID+1))
		if questionID != 0 and questionID != 4:
			print(stats.kruskal(correct[questionID][0],correct[questionID][1],correct[questionID][2],correct[questionID][3]))
	
	
	for opinionID in range(0,opinionCount):
		print("Opinion " + str(opinionID+1))
		if opinionID > 1:
			print(stats.kruskal(opinion[opinionID][0],opinion[opinionID][1],opinion[opinionID][2],opinion[opinionID][3]))
	
	for questionID in range(0,questionCount):
		print("Question Time " + str(questionID+1))
		print(stats.kruskal(questionTime[questionID][0],questionTime[questionID][1],questionTime[questionID][2],questionTime[questionID][3]))
