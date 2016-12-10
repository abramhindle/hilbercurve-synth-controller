s.boot;

x = nil;
q = nil;
y = nil;
z = nil;
u = nil;
v = nil;
r = nil;

s.waitForBoot {
	x = nil;
	q = nil;
	y = nil;
	z = nil;
	u = nil;
	v = nil;
	r = nil;
	SynthDef(\pinknoiser,{arg out=0,lpf=0.0,shift=0.0,pitchdisp=0.0,timedisp=0.0;
		Out.ar(out,
			4.0*
			PitchShift.ar(		
				LPF.ar(PinkNoise.ar(),LinLin.kr(lpf,0,1.0,30,200)),	// stereo audio input
				0.1, 			// grain size
				LinLin.kr(shift,0,1.0,0.5,16), // mouse x controls pitch shift ratio
				pitchdisp, 				// pitch dispersion
				LinExp.kr(timedisp,0,1,0.001,0.1)	// time dispersion
			)!2
		);
	}).add;
	SynthDef(\noiser,{arg out=0,lpf=0.0,shift=0.0,pitchdisp=0.0,timedisp=0.0,pink=0.0,white=0.0,brown=0.0,rq=0.5;
		Out.ar(out,
			4.0*
			PitchShift.ar(		
				RLPF.ar(
					(pink*PinkNoise.ar() + white*WhiteNoise.ar()+ brown*BrownNoise.ar())/3.0
					,LinLin.kr(lpf,0,1.0,30,200)
					,Lag.kr(rq,0.1)
				),	// stereo audio input
				0.1, 			// grain size
				LinLin.kr(shift,0,1.0,0.5,16), // mouse x controls pitch shift ratio
				pitchdisp, 				// pitch dispersion
				LinExp.kr(timedisp,0,1,0.001,0.1)	// time dispersion
			)!2
		);
	}).add;
	SynthDef(\lsiner,{arg out=0,osc1=0.0,osc2=0.0,osc3=0.0,osc4=0.0;
		Out.ar(out,
			LPF.ar(
				(SinOsc.ar(LinExp.kr(osc1,0,1.0,40,200)) +
					SinOsc.ar(LinExp.kr(osc2,0,1.0,40,200)) + 
					SinOsc.ar(LinExp.kr(osc3,0,1.0,120,280))) / 3.0
				,LinLin.kr(osc4,0,1.0,40,280)
			)
		);
	}).add;
	SynthDef(\rsiner,{arg out=0,osc1=0.0,osc2=0.0,osc3=0.0,osc4=0.0;
		Out.ar(out,
			Clip.ar(
				RLPF.ar((SinOsc.ar(LinExp.kr(osc1,0,1.0,40,200)) + 
					SinOsc.ar(LinExp.kr(osc3,0,1.0,120,280))) / 3.0
					,LinLin.kr(osc3,0,1.0,40,280)
					,LinExp.kr(osc4,0,1.0,0.01,1.0)
				),
				-0.9,0.9)
		);
	}).add;
	SynthDef("blipsaw",
		{ 
			arg out=0,freq=60,fadd=0,ffreq=10,ffmul=10,ffadd=0,harmfreq=0.1,hmul=3,amp=0.2,delaytime=0.0,decaytime=0.0;
			Out.ar(out,
				Clip.ar(
					0.8*CombC.ar(
						Blip.ar(LinLin.kr(freq,0,1.0,10,80).midicps
							+LinLin.kr(fadd,0,1.0,-30.0,30.0)+
							LFSaw.kr(LinLin.kr(ffreq,0,1.0,0.1,30),mul:LinLin.kr(ffmul,0,1.0,0.1,2))!2,
							LFSaw.kr(LinLin.kr(harmfreq,0,1.0,0.01,1.0),mul:
								LinLin.kr(hmul,0,1.0,0.01,0.3)),
							LinLin.kr(amp,0,1.0,0.0,0.9)),
						2.1,
						LinLin.kr(delaytime,0,1.0,0.01,1.5),//0.2,
						LinLin.kr(decaytime,0,1.0,0.01,2.0)//1.0
					)
					, -0.5, 0.5
				)
			)
		}
	).load(s);
	SynthDef(\hydro1, {
		arg out=0,freq=440;
		var n = (2..10);
		Out.ar(0,
			(n.collect {arg i; SinOsc.ar( (1 - (1/(i*i))) * freq )}).sum
		)
	}).add;
	SynthDef(\hydro4, {
		|out=0,amp=1.0,freq1=440,freq2=200,freq3=320,lpf=60,rq=0.5|
		var nsize,n = (2..10);
		nsize = n.size;
		Out.ar(0,
			Lag.kr(amp,0.1) * 
			RLPF.ar((
				n.collect {arg i; 
					SinOsc.ar( (1.0 - (1/(i*i))) * 2*freq1 ) +
					SinOsc.ar( (1.0 - (1/(i*i))) * freq2 ) +
					SinOsc.ar( ((1/4) - (1/((i+1)*(i+1)))) * freq3)
				}).sum / (3 * nsize)
				,Lag.kr(lpf,0.1)
				,Lag.kr(rq,0.1))
		)
	}).add;
	SynthDef(\bubbles, { 
		arg out=0, sawfreq=0.4, sawmul=24.0, sawfreq2=8.0, 
	    sawfreq3=7.23, sawmul2=3, sawadd2=80, sinemul=0.04,
	    decaytime=0.2, combmul = -4, amp=0.3;
		var f, zout;
		f = LFSaw.kr(sawfreq, 0, sawmul, LFSaw.kr([sawfreq2,sawfreq3], 0, sawmul2, sawadd2)).midicps;
		zout = CombN.ar(SinOsc.ar(f, 0, sinemul), 1.0, decaytime, combmul); // echoing sine wave
		Out.ar(out, Clip.ar((zout *amp)!2,-0.9,0.9));
	}).load(s);
	// pinknoiser
	OSCFunc.newMatching({|msg|
		x.set(\lpf,msg[1]);
		x.set(\shift,msg[2]);
		x.set(\pitchdisp,msg[3]);
		x.set(\timedisp,msg[4]);
	}, '/mouse4');
	// noiser
	OSCFunc.newMatching({|msg|
		q.set(\lpf,msg[1]);
		q.set(\shift,msg[2]);
		q.set(\pitchdisp,msg[3]);
		q.set(\timedisp,msg[4]);
		q.set(\white,msg[5]);//(msg[5]>0.5).asInteger);
		q.set(\pink ,msg[6]);//(msg[6]>0.5).asInteger);
		q.set(\brown,msg[7]);//(msg[7]>0.5).asInteger);
		q.set(\rq,msg[8]);
	}, '/mouse8');
	// lsiner
	OSCFunc.newMatching({|msg|
		msg.postln;
		y.set(\osc1,msg[1]);
		y.set(\osc2,msg[2]);
		y.set(\osc3,msg[3]);
		y.set(\osc4,msg[4]);
	}, '/mouse4');
	// rsiner
	OSCFunc.newMatching({|msg|
		msg.postln;
		z.set(\osc1,msg[1]);
		z.set(\osc2,msg[2]);
		z.set(\osc3,msg[3]);
		z.set(\osc4,msg[4]);
	}, '/mouse4');
	// blipsaw
	OSCFunc.newMatching({|msg|
		msg.postln;
		u.set(\freq,msg[1]);
		u.set(\fadd,msg[2]);
		u.set(\ffreq,msg[3]);
		u.set(\ffmul,msg[4]);
		u.set(\ffadd,msg[5]);
		u.set(\harmfreq,msg[6]);
		u.set(\hmul,msg[7]);
		u.set(\amp,msg[8]);
		u.set(\delaytime,msg[9]);
		u.set(\decaytime,msg[10]);
	}, '/mouse10');
	// hydro4
	OSCFunc.newMatching({|msg|
		msg.postln;
		v.set(\rq,msg[6].linlin(0,1.0,0.0,1.0));
		v.set(\lpf,msg[5].linlin(0,1,10,125).midicps);
		v.set(\freq1,msg[4].linlin(0,1,20,70).midicps);
		v.set(\freq2,msg[2].linlin(0,1,30,90).midicps);
		v.set(\freq3,msg[3].linlin(0,1,50,110).midicps);
		v.set(\amp,(msg[1]>0.25).asInteger * msg[4] - 0.25);
	}, '/mouse6');
	// bubbles
	OSCFunc.newMatching({|msg|
		msg.postln;
		r.set(\sawfreq,  msg[10].linlin(0,1.0,0.01,3.0));
		r.set(\sawmul,   msg[2].linlin(0,1.0,10,96.0));
		r.set(\sawfreq2, msg[3].linlin(0,1.0,0.01,32.0));
		r.set(\sawfreq3, msg[4].linlin(0,1.0,0.01,16.0));
		r.set(\sawmul2,  msg[5].linlin(0,1.0,0.01,5.0));
		r.set(\sawadd2,  msg[6].linlin(0,1.0,10,160));
		r.set(\sinemul,  msg[7].linlin(0,1.0,0.001,0.2));
		r.set(\decaytime,  msg[8].linlin(0,1.0,0.05,0.6));
		r.set(\combmul,  msg[9].linlin(0,1.0,-8.0,8.0));
		r.set(\amp,  msg[1].linlin(0,1.0,0.0,1.0));
	}, '/mouse10');
	OSCFunc.newMatching({|msg|
		if(x.isNil,{
			x = Synth(\pinknoiser);
		},{ x.free; x = nil;}
		);
	},"/toggle/pinknoiser");
	OSCFunc.newMatching({|msg|
		if(q.isNil,{
			q = Synth(\noiser);
		},{ q.free; q = nil;}
		);
	},"/toggle/noiser");
	OSCFunc.newMatching({|msg|
		if(y.isNil,{
			y = Synth(\lsiner);
		},{ y.free; y = nil;}
		);
	},"/toggle/lsiner");
	OSCFunc.newMatching({|msg|
		if(z.isNil,{
			z = Synth(\rsiner);
		},{ z.free; z = nil;}
		);
	},"/toggle/rsiner");
	OSCFunc.newMatching({|msg|
		if(u.isNil,{
			u = Synth(\blipsaw);
		},{ u.free; u = nil;}
		);
	},"/toggle/blipsaw");
	OSCFunc.newMatching({|msg|
		if(v.isNil,{
			v = Synth(\hydro4);
		},{ v.free; v = nil;}
		);
	},"/toggle/hydro4");
	OSCFunc.newMatching({|msg|
		if(r.isNil,{
			r = Synth(\bubbles);
		},{ r.free; r = nil;}
		);
	},"/toggle/bubbles");
	
};

/*
x = Synth(\pinknoiser);
q = Synth(\noiser);
y = Synth(\lsiner);
z = Synth(\rsiner);
u = Synth(\blipsaw);
v = Synth(\hydro4);
r = Synth(\bubbles);
*/
//if(x.isNil,{"Nil".postln},{"N".postln})
//x,q,y,z,u,v,r

	
