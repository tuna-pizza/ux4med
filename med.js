//import { medGraph, medEdge, medVertex, medAnimation } from 'medGraph.js';
//import { example1 } from 'medExampleGenerator.js';

function draw(canvasID,style,speed)
{
	const canvas = document.getElementById(canvasID);
	graph = example2(style,speed); 
	canvas.height = canvas.width = 400;
	var startTime = +new Date();
	if (canvas.getContext) 
	{		
		const ctx = canvas.getContext("2d");
		setInterval(function () {redraw(graph,ctx,canvas,startTime)}, 15);
		
		/*
		drawVertex(20,20,ctx);
		drawVertex(100,80,ctx);
		drawEdge(20,20,100,80,ctx);*/
	}
}

function redraw(graph,ctx,canvas,startTime)
{
	var currentTime = +new Date();
	time = currentTime - startTime;
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	for (const e of graph.getEdges())
	{
		drawEdge(e,ctx,time)
	}
	for (const v of graph.getVertices())
	{
		drawVertex(v,ctx);
	}
}

function drawVertex(v,ctx)
{
	x = v.getX();
	y = v.getY();
	ctx.fillStyle = v.getColor();
	ctx.beginPath();
	ctx.ellipse(x, y, 3, 3, 0, 0, Math.PI * 2);
	ctx.fill();
}

function drawEdge(e,ctx,time)
{
	x1 = e.getV1().getX();
	y1 = e.getV1().getY();
	x2 = e.getV2().getX();
	y2 = e.getV2().getY();
	midX = (x1+x2)/2;
	midY = (y1+y2)/2;
	startX1 = x1 + (midX-x1)*e.getMinLength();
	startY1 = y1 + (midY-y1)*e.getMinLength();
	startX2 = x2 + (midX-x2)*e.getMinLength();
	startY2 = y2 + (midY-y2)*e.getMinLength();
	drawn = false;
	for (a of e.getAnimations())
	{
		timeForA = time%a.getPeriod() - a.getStartTime();
		//morphDuration = 200;
		morphDuration = Math.sqrt(Math.pow(midX-startX1,2)+Math.pow(midY-startY1,2))/a.getSpeed();
		totalDuration = 2*morphDuration + a.getFullLengthTime();
		currentLength = 0;
		if (a.getMorphType() === "COMPLETE")
		{
			drawn = true;
			currentLength = 1;
		}
		if (a.getMorphType() === "PED")
		{
			drawn = true;
			currentLength = 0;
		}
		if (timeForA >= 0 && timeForA <= totalDuration)
		{
			drawn = true;
			if (a.getMorphType() === "LINEAR")
			{
				
				if (timeForA < morphDuration)
				{
					currentLength = (timeForA)/morphDuration;	
				}
				else
				{
					if (timeForA > morphDuration + a.getFullLengthTime())
					{
						currentLength = 1 - (timeForA - (morphDuration+a.getFullLengthTime()))/morphDuration;
					}
					else
					{
						currentLength = 1;
					}
				}
			}
			if (a.getMorphType() === "SINE")
			{
				
				if (timeForA < morphDuration)
				{
					currentLength = Math.sin(((timeForA)/morphDuration)*Math.PI/2);	
				}
				else
				{
					if (timeForA > morphDuration + a.getFullLengthTime())
					{
						currentLength = Math.sin((1 - (timeForA - (morphDuration+a.getFullLengthTime()))/morphDuration)*Math.PI/2);
					}
					else
					{
						currentLength = 1;
					}
				}
			}	
			if (a.getMorphType() === "REVERSESINE")
			{
				
				if (timeForA < morphDuration)
				{
					currentLength = 1-Math.cos(((timeForA)/morphDuration)*Math.PI/2);	
				}
				else
				{
					if (timeForA > morphDuration + a.getFullLengthTime())
					{
						currentLength = 1-Math.cos((1 - (timeForA - (morphDuration+a.getFullLengthTime()))/morphDuration)*Math.PI/2);
					}
					else
					{
						currentLength = 1;
					}
				}
			}
			if (a.getMorphType() === "COSINE")
			{
				
				if (timeForA < morphDuration)
				{
					currentLength = (-Math.cos(((timeForA)/morphDuration)*Math.PI)+1)/2;	
				}
				else
				{
					if (timeForA > morphDuration + a.getFullLengthTime())
					{
						currentLength = (-Math.cos((1 - (timeForA - (morphDuration+a.getFullLengthTime()))/morphDuration)*Math.PI)+1)/2;
					}
					else
					{
						currentLength = 1;
					}
				}
			}				
		}
		currentX1 = startX1 + (midX-startX1)*currentLength;
		currentY1 = startY1 + (midY-startY1)*currentLength;
		currentX2 = startX2 + (midX-startX2)*currentLength;
		currentY2 = startY2 + (midY-startY2)*currentLength;
		ctx.moveTo(x1, y1);
		ctx.lineTo(currentX1, currentY1);
		ctx.stroke();
		ctx.moveTo(x2, y2);
		ctx.lineTo(currentX2, currentY2);
		ctx.stroke();
	}
	if (!drawn)
	{
		ctx.moveTo(x1, y1);
		ctx.lineTo(startX1, startY1);
		ctx.stroke();
		ctx.moveTo(x2, y2);
		ctx.lineTo(startX2, startY2);
		ctx.stroke();
	}
}


class medGraph 
{
	constructor() 
	{
		this.vertices = new Map();
		this.edges = new Set();
	}
	addVertex(vertex)
	{
		if (!this.vertices.has(vertex.id)) 
		{
			this.vertices.set(vertex.id,vertex);
		}
	}
	addEdge(edge)
	{
		if (this.vertices.has(edge.getV1().getID()) && this.vertices.has(edge.getV2().getID()))
		{
			this.edges.add(edge);
		}
	}
	getVertices()
	{
		return this.vertices.values();
	}
	getEdges()
	{
		return this.edges.values();
	}
}

class medAnimation
{
	constructor(start_time,speed,fullLengthTime,period,morph_type)
	{
		this.start_time = start_time;
		this.speed = speed;
		this.fullLengthTime = fullLengthTime;
		this.period = period;
		this.morph_type = morph_type;
	}
	getStartTime()
	{
		return this.start_time;
	}
	getSpeed()
	{
		return this.speed;
	}
	getFullLengthTime()
	{
		return this.fullLengthTime;
	}
	getPeriod()
	{
		return this.period;
	}
	getMorphType()
	{
		return this.morph_type;
	}
}

class medEdge
{
	constructor(v1,v2,minLength)
	{
		this.v1 = v1;
		this.v2 = v2;
		this.minLength = minLength;
		this.animations = new Set();
		
	}
	addAnimation(animation)
	{
		this.animations.add(animation);
	}
	getV1()
	{
		return this.v1;
	}
	getV2()
	{
		return this.v2;
	}
	getMinLength()
	{
		return this.minLength;
	}
	getAnimations()
	{
		return this.animations.values();
	}
}

class medVertex
{
	constructor(id,x,y,color)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.color = color;
	}
	getID()
	{
		return this.id;
	}
	getX()
	{
		return this.x;
	}
	getY()
	{
		return this.y;
	}
	getColor()
	{
		return this.color;
	}
}

function example1 ()
{
	graph = new medGraph();
	v1 = new medVertex("1",20,20,"black");
	v2 = new medVertex("2",100,80,"black");
	graph.addVertex(v1);
	graph.addVertex(v2);
	e1 = new medEdge(v1,v2,0.25);
	a1 = new medAnimation(0,500,1000,"LINEAR");
	e1.addAnimation(a1);
	graph.addEdge(e1);
	return graph;
}

function example2 (style,speed)
{
	graph = new medGraph();
	v1 = new medVertex("1",200,25,"black");
	v2 = new medVertex("2",323.7,76.2,"black");
	v3 = new medVertex("3",375,200,"black");
	v4 = new medVertex("4",323.7,323.7,"black");
	v5 = new medVertex("5",200,375,"black");
	v6 = new medVertex("6",76.2,323.7,"black");
	v7 = new medVertex("7",25,200,"black");
	v8 = new medVertex("8",76.2,76.2,"black");
	graph.addVertex(v1);
	graph.addVertex(v2);
	graph.addVertex(v3);
	graph.addVertex(v4);
	graph.addVertex(v5);
	graph.addVertex(v6);
	graph.addVertex(v7);
	graph.addVertex(v8);
	e1 = new medEdge(v1,v2,0.25);
	e2 = new medEdge(v1,v3,0.25);
	e3 = new medEdge(v1,v4,0.25);
	e4 = new medEdge(v1,v5,0.25);
	e5 = new medEdge(v1,v6,0.25);
	e6 = new medEdge(v1,v7,0.25);
	e7 = new medEdge(v1,v8,0.25);
	
	
	e8 = new medEdge(v2,v3,0.25);
	e9 = new medEdge(v2,v4,0.25);
	e10 = new medEdge(v2,v5,0.25);
	e11 = new medEdge(v2,v6,0.25);
	e12 = new medEdge(v2,v7,0.25);
	e13 = new medEdge(v2,v8,0.25); 
	
	e14 = new medEdge(v3,v4,0.25);
	e15 = new medEdge(v3,v5,0.25);
	e16 = new medEdge(v3,v6,0.25);
	e17 = new medEdge(v3,v7,0.25);
	e18 = new medEdge(v3,v8,0.25); 
	
	e19 = new medEdge(v4,v5,0.25);
	e20 = new medEdge(v4,v6,0.25);
	e21 = new medEdge(v4,v7,0.25);
	e22 = new medEdge(v4,v8,0.25);
	
	e23 = new medEdge(v5,v6,0.25);
	e24 = new medEdge(v5,v7,0.25);
	e25 = new medEdge(v5,v8,0.25);
	
	e26 = new medEdge(v6,v7,0.25);
	e27 = new medEdge(v6,v8,0.25);
	
	e28 = new medEdge(v7,v8,0.25);
	a1 = new medAnimation(0,0.05*speed,100,20000/speed,style);
	a2 = new medAnimation(2000/speed,0.05*speed,100,20000/speed,style);
	a3 = new medAnimation(4000/speed,0.05*speed,100,20000/speed,style);
	a4 = new medAnimation(6000/speed,0.05*speed,100,20000/speed,style);
	a5 = new medAnimation(8000/speed,0.05*speed,100,20000/speed,style);
	a6 = new medAnimation(10000/speed,0.05*speed,100,20000/speed,style);
	a7 = new medAnimation(12000/speed,0.05*speed,100,20000/speed,style);
	e1.addAnimation(a1);
	e2.addAnimation(a1);
	e3.addAnimation(a1);
	e4.addAnimation(a1);
	e5.addAnimation(a1);
	e6.addAnimation(a1);
	e7.addAnimation(a1);
	e8.addAnimation(a5);
	e9.addAnimation(a5);
	e10.addAnimation(a5);
	e11.addAnimation(a5);
	e12.addAnimation(a5);
	e13.addAnimation(a5);
	e14.addAnimation(a3);
	e15.addAnimation(a3);
	e16.addAnimation(a3);
	e17.addAnimation(a3);
	e18.addAnimation(a3);
	e19.addAnimation(a7);
	e20.addAnimation(a7);
	e21.addAnimation(a7);
	e22.addAnimation(a7);
	e23.addAnimation(a2);
	e24.addAnimation(a2);
	e25.addAnimation(a2);
	e26.addAnimation(a6);
	e27.addAnimation(a6);
	e28.addAnimation(a4);
	graph.addEdge(e1);
	graph.addEdge(e2);
	graph.addEdge(e3);
	graph.addEdge(e4);
	graph.addEdge(e5);
	graph.addEdge(e6);
	graph.addEdge(e7);
	graph.addEdge(e8);
	graph.addEdge(e9);
	graph.addEdge(e10);
	graph.addEdge(e11);
	graph.addEdge(e12);
	graph.addEdge(e13);
	graph.addEdge(e14);
	graph.addEdge(e15);
	graph.addEdge(e16);
	graph.addEdge(e17);
	graph.addEdge(e18);
	graph.addEdge(e19);
	graph.addEdge(e20);
	graph.addEdge(e21);
	graph.addEdge(e22);
	graph.addEdge(e23);
	graph.addEdge(e24);
	graph.addEdge(e25);
	graph.addEdge(e26);
	graph.addEdge(e27);
	graph.addEdge(e28);
	return graph;
}