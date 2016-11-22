s.boot;
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

x = Synth(\pinknoiser);
OSCFunc.newMatching({|msg|
	x.set(\lpf,msg[1]);
	x.set(\shift,msg[2]);
	x.set(\pitchdisp,msg[3]);
	x.set(\timedisp,msg[4]);
}, '/mouse');


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

x = Synth(\noiser);
OSCFunc.newMatching({|msg|
	x.set(\lpf,msg[1]);
	x.set(\shift,msg[2]);
	x.set(\pitchdisp,msg[3]);
	x.set(\timedisp,msg[4]);
	x.set(\white,msg[5]);//(msg[5]>0.5).asInteger);
	x.set(\pink ,msg[6]);//(msg[6]>0.5).asInteger);
	x.set(\brown,msg[7]);//(msg[7]>0.5).asInteger);
	x.set(\rq,msg[8]);
}, '/mouse');





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
y = Synth(\lsiner);
OSCFunc.newMatching({|msg|
	msg.postln;
	y.set(\osc1,msg[1]);
	y.set(\osc2,msg[2]);
	y.set(\osc3,msg[3]);
	y.set(\osc4,msg[4]);
}, '/mouse');
y.set(\osc1,0.9);




SynthDef(\rsiner,{arg out=0,osc1=0.0,osc2=0.0,osc3=0.0,osc4=0.0;
	Out.ar(out,
		RLPF.ar(SinOsc.ar(LinExp.kr(osc1,0,1.0,40,200)) + 
			SinOsc.ar(LinExp.kr(osc3,0,1.0,120,280))) / 3.0
		,LinLin(osc3,0,1.0,40,280)
		,LinExp(osc4,0,1.0,0.01,1.0)
	);
}).add;
y = Synth(\rsiner);
OSCFunc.newMatching({|msg|
	msg.postln;
	y.set(\osc1,msg[1]);
	y.set(\osc2,msg[2]);
	y.set(\osc3,msg[3]);
	y.set(\osc4,msg[4]);
}, '/mouse');
y.set(\osc1,0.9);



SynthDef("blipsaw",
	{ 
		arg out=0,freq=60,fadd=0,ffreq=10,ffmul=10,ffadd=0,harmfreq=0.1,hmul=3,amp=0.2,delaytime=0.0,decaytime=0.0;
		Out.ar(out,
			CombC.ar(
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
		)
	}
).load(s);
y = Synth(\blipsaw);
OSCFunc.newMatching({|msg|
	msg.postln;
	y.set(\freq,msg[1]);
	y.set(\fadd,msg[2]);
	y.set(\ffreq,msg[3]);
	y.set(\ffmul,msg[4]);
	y.set(\ffadd,msg[5]);
	y.set(\harmfreq,msg[6]);
	y.set(\hmul,msg[7]);
	y.set(\amp,msg[8]);
	y.set(\delaytime,msg[9]);
	y.set(\decaytime,msg[10]);
}, '/mouse');


SynthDef(\hydro1, {
	arg out=0,freq=440
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
y = Synth(\hydro4);
OSCFunc.newMatching({|msg|
	msg.postln;
	y.set(\rq,msg[6].linlin(0,1.0,0.0,1.0));
	y.set(\lpf,msg[5].linlin(0,1,10,125).midicps);
	y.set(\freq1,msg[4].linlin(0,1,20,70).midicps);
	y.set(\freq2,msg[2].linlin(0,1,30,90).midicps);
	y.set(\freq3,msg[3].linlin(0,1,50,110).midicps);
	y.set(\amp,(msg[1]>0.25).asInteger * msg[4] - 0.25);
}, '/mouse');
