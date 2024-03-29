////////////////////   VPAID Bridge Wrapper   ////////////////////////////////
adVPAIDWrapper=function(){
	this._creative=getVPAIDAd();
	//this._ui=getUI();
	this.timer=null;
	adVPAIDWrapper.prototype.setVpaidClient=function(vpaidClient){
		this._vpaidClient=vpaidClient;
	}
	adVPAIDWrapper.prototype.handshakeVersion=function(version){	
		var result=this._creative.handshakeVersion(version);
		android.handshakeVersionResult(result);
		return result;
	}
	adVPAIDWrapper.prototype.initAd=function(width,height,viewMode,desiredBitrate,creativeData,environmentVars){		
		this._creative.initAd(width,height,viewMode,desiredBitrate,creativeData,environmentVars);
		android.initAdResult();
	};
	adVPAIDWrapper.prototype.onAdPaused=function(){
			console.log("onAdPaused");
			this._vpaidClient.vpaidAdPaused();
	};
	adVPAIDWrapper.prototype.onAdPlaying=function(){
			console.log("onAdPlaying");
			this._vpaidClient.vpaidAdPlaying();
	};
	adVPAIDWrapper.prototype.onAdError=function(message){
		console.log("onAdError: "+message);
		this._vpaidClient.vpaidAdError(message);
	};
	adVPAIDWrapper.prototype.onAdLog=function(message){
		console.log("onAdLog: "+message);
		this._vpaidClient.vpaidAdLog(message);
	};
	adVPAIDWrapper.prototype.onAdUserAcceptInvitation=function(){
		console.log("onAdUserAcceptInvitation");
		this._vpaidClient.vpaidAdUserAcceptInvitation();	
	};
	adVPAIDWrapper.prototype.onAdUserMinimize=function(){
		console.log("onAdUserMinimize");
		this._vpaidClient.vpaidAdUserMinimize();
	};
	adVPAIDWrapper.prototype.onAdUserClose=function(){
		console.log("onAdUserClose");
		this._vpaidClient.vpaidAdUserClose();
	};
	adVPAIDWrapper.prototype.onAdSkippableStateChange=function(){
		console.log("Ad Skippable State Changed to: "+this._creative.getAdSkippableState());
		//this._ui.showSkipButton(this.getAdSkippableState());
		this.getAdSkippableState(); //wilder 2019 , pass skip state to app through getstateresult()
		this._vpaidClient.vpaidAdSkippableStateChange();
	};
	adVPAIDWrapper.prototype.onAdExpandedChange=function(){
		console.log("Ad Expanded Changed to: "+this._creative.getAdExpanded());
		this._vpaidClient.vpaidAdExpandedChange();
	};
	adVPAIDWrapper.prototype.getAdExpanded=function(){
		console.log("getAdExpanded");
		var result=this._creative.getAdExpanded();
		android.getAdExpandedResult(result);
	};
	adVPAIDWrapper.prototype.getAdSkippableState=function(){
		console.log("getAdSkippableState");
		if (this._creative.getAdSkippableState && typeof this._creative.getAdSkippableState == "function") {
		    var result=this._creative.getAdSkippableState();
		    android.getAdSkippableStateResult(result);
		}else
		    return false;
	};
	adVPAIDWrapper.prototype.onAdSizeChange=function(){
		console.log("Ad size changed to: w="+this._creative.getAdWidth()+" h="+this._creative.getAdHeight());
		this._vpaidClient.vpaidAdSizeChange();
	};
	adVPAIDWrapper.prototype.onAdDurationChange=function(){
		if(this.handshakeVersion()>=2){
			//this._ui.moveProgress(this._creative.getAdRemainingTime(),this._creative.getAdDuration());
		}
		this._vpaidClient.vpaidAdDurationChange();
	};
	adVPAIDWrapper.prototype.onAdRemainingTimeChange=function(){
		if(this.handshakeVersion()<2){
			//this._ui.moveProgress(this._creative.getAdRemainingTime(),this._creative.getAdDuration());
		}
		this._vpaidClient.vpaidAdRemainingTimeChange();
	};
	adVPAIDWrapper.prototype.getAdRemainingTime=function(){
		console.log("getAdRemainingTime");
		var result=this._creative.getAdRemainingTime();
		android.getAdRemainingTimeResult(result);
	};
	adVPAIDWrapper.prototype.onAdImpression=function(){
			console.log("Ad Impression");
			this._vpaidClient.vpaidAdImpression();
	};
	adVPAIDWrapper.prototype.onAdClickThru=function(url,id,playerHandles){
			console.log("Clickthrough portion of the ad was clicked");
			var adjustedUrl=url;
			if(adjustedUrl==undefined)
				adjustedUrl=""
			this._vpaidClient.vpaidAdClickThruIdPlayerHandles(adjustedUrl,id,playerHandles);
	};
	adVPAIDWrapper.prototype.onAdInteraction=function(id){
		console.log("A non-clickthrough event has occured");
		this._vpaidClient.vpaidAdInteraction(id);
	};
	adVPAIDWrapper.prototype.onAdVideoStart=function(){
		console.log("Video 0% completed");
		this._vpaidClient.vpaidAdVideoStart();
		document.getElementById("black-screen")&&(document.getElementById("black-screen").style.display="none");
		};
	adVPAIDWrapper.prototype.onAdVideoFirstQuartile=function(){
		console.log("Video 25% completed");
		this._vpaidClient.vpaidAdVideoFirstQuartile();
	};
	adVPAIDWrapper.prototype.onAdVideoMidpoint=function(){
		console.log("Video 50% completed");
		this._vpaidClient.vpaidAdVideoMidpoint();
	};
	adVPAIDWrapper.prototype.onAdVideoThirdQuartile=function(){
		console.log("Video 75% completed");
		this._vpaidClient.vpaidAdVideoThirdQuartile();
	};
	adVPAIDWrapper.prototype.onAdVideoComplete=function(){
		console.log("Video 100% completed");
		this._vpaidClient.vpaidAdVideoComplete();
	};
	adVPAIDWrapper.prototype.onAdLinearChange=function(){
		console.log("Ad linear has changed: "+this._creative.getAdLinear());
		this._vpaidClient.vpaidAdLinearChange();
	};
	adVPAIDWrapper.prototype.getAdLinear=function(){
		console.log("getAdLinear");
		var result=this._creative.getAdLinear();
		android.getAdLinearResult(result);
	};
	adVPAIDWrapper.prototype.getAdDuration=function(){
		console.log("getAdDuration");
		var result=this._creative.getAdDuration();
		android.getAdDurationResult(result);
	};
	adVPAIDWrapper.prototype.onAdLoaded=function(){
		console.log("ad has been loaded");
		//this._ui.createSkipButton();
		//this._ui.showSkipButton(this.getAdSkippableState());
        this.getAdSkippableState(); //wilder 2019
        this.getAdVolume();     //wilder 2019
		this._vpaidClient.vpaidAdLoaded();
	};
	adVPAIDWrapper.prototype.onAdStarted=function(){
		console.log("Ad has started");
		//var res=this._creative.getAdDuration();
        //android.getAdDurationResult(res);
		this.timer=setInterval(function(){
			//this._ui.moveProgress(this._creative.getAdRemainingTime(),this._creative.getAdDuration());
			var res=this._creative.getAdDuration();
            android.getAdDurationResult(res);
			var result=this._creative.getAdRemainingTime();
			this._vpaidClient.getAdRemainingTimeResult(result);

			}.bind(this),500);

		this._vpaidClient.vpaidAdStarted();
	};
	adVPAIDWrapper.prototype.onAdStopped=function(){
		console.log("Ad has stopped");
		clearInterval(this.timer);
		this._vpaidClient.vpaidAdStopped();
	};
	adVPAIDWrapper.prototype.onAdSkipped=function(){
		console.log("Ad was skipped");
		this._creative.stopAd();
		this._vpaidClient.vpaidAdSkipped();
	};
	adVPAIDWrapper.prototype.setAdVolume=function(val){
	this._creative.setAdVolume(val);
	};
	adVPAIDWrapper.prototype.getAdVolume=function(){
	var result=this._creative.getAdVolume();
	android.getAdVolumeResult(result);
	};
	adVPAIDWrapper.prototype.onAdVolumeChange=function(){
	console.log("Ad Volume has changed to - "+this._creative.getAdVolume());
	this._vpaidClient.vpaidAdVolumeChanged();
	};
	adVPAIDWrapper.prototype.startAd=function(){
	//this._ui.createProgressBar();
	this._creative.startAd();
	};
	adVPAIDWrapper.prototype.skipAd=function(){
	this._creative.skipAd();
	};
	adVPAIDWrapper.prototype.stopAd=function(){
	this._creative.stopAd();
	};
	adVPAIDWrapper.prototype.resizeAd=function(width,height,viewMode){
	this._creative.resizeAd(width,height,viewMode);
	};
	adVPAIDWrapper.prototype.pauseAd=function(){
	this._creative.pauseAd();
	};
	adVPAIDWrapper.prototype.resumeAd=function(){
	this._creative.resumeAd();
	};
	adVPAIDWrapper.prototype.expandAd=function(){
	this._creative.expandAd();
	};
	adVPAIDWrapper.prototype.collapseAd=function(){
	this._creative.collapseAd();
	};
	adVPAIDWrapper.prototype.setCallbacksForCreative=function(){
			var callbacks=
			{
			'AdStarted':this.onAdStarted,
			'AdStopped':this.onAdStopped,
			'AdSkipped':this.onAdSkipped,
			'AdLoaded':this.onAdLoaded,
			'AdLinearChange':this.onAdLinearChange,
			'AdSizeChange':this.onAdSizeChange,
			'AdExpandedChange':this.onAdExpandedChange,
			'AdSkippableStateChange':this.onAdSkippableStateChange,
			'AdDurationChange':this.onAdDurationChange,
			'AdRemainingTimeChange':this.onAdRemainingTimeChange,
			'AdVolumeChange':this.onAdVolumeChange,
			'AdImpression':this.onAdImpression,
			'AdClickThru':this.onAdClickThru,
			'AdInteraction':this.onAdInteraction,
			'AdVideoStart':this.onAdVideoStart,
			'AdVideoFirstQuartile':this.onAdVideoFirstQuartile,
			'AdVideoMidpoint':this.onAdVideoMidpoint,
			'AdVideoThirdQuartile':this.onAdVideoThirdQuartile,
			'AdVideoComplete':this.onAdVideoComplete,
			'AdUserAcceptInvitation':this.onAdUserAcceptInvitation,
			'AdUserMinimize':this.onAdUserMinimize,
			'AdUserClose':this.onAdUserClose,
			'AdPaused':this.onAdPaused,
			'AdPlaying':this.onAdPlaying,
			'AdError':this.onAdError,
			'AdLog':this.onAdLog
			};
			for(var eventName in callbacks){
				this._creative.subscribe(callbacks[eventName],eventName,this);
			}
	};
	adVPAIDWrapper.prototype.onAdSkipPress=function(){
		this._creative.skipAd();
	}
	adVPAIDWrapper.prototype.setCallbacksForUI=function(){
	    /*
		var callbacks={'AdSkipped':this.onAdSkipPress,};
		for(var eventName in callbacks){
			this._ui.subscribe(callbacks[eventName],eventName,this);
		}
		*/
	};
}

initVpaidWrapper=function(){
	adVPAIDWrapperInstance=new adVPAIDWrapper();
	adVPAIDWrapperInstance.setCallbacksForCreative();
	adVPAIDWrapperInstance.setVpaidClient(android);
	adVPAIDWrapperInstance.setCallbacksForUI()
	android.wrapperReady();
}

/*
////////////////////   VPAID UI   ////////////////////////////////
adVPAIDUI=function()
{
	this._eventsCallbacks={};
	adVPAIDUI.prototype.createSkipButton=function(){
		var adButton=document.createElement('div');
		adButton.addEventListener('click',this.skipAd.bind(this),false);
		adButton.onclick=function(e){
			e.stopPropagation();
		};
	var vpaidContainer=document.getElementById('adview-slot');
	vpaidContainer.appendChild(adButton);
	adButton.style.position="fixed";
	adButton.transform="translate(0%, 0%)";
	adButton.style.right=0;
	adButton.style.top=0;
	adButton.style.width="60px";
	adButton.style.height="60px";
	adButton.style.backgroundSize="19px";
	adButton.style.opacity=1;
	adButton.style.background="url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABUAAAAVCAYAAACpF6WWAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAolJREFUeNqklctrE1EUxjs1KepYnx0TsUhDFEUNAReFkCxKNkZ0F/L4C0JCwCgKMUJWLiSItb5ITAIKKTQPcaWEuoguEgNdBVI31iQIiklRWm2mlBYcvyN3Qi22ncEDP7hzz51zzz3fmTtc3/9bP+DBL0mSRJrgmGMA7GXjLlhREfAh2AkkcBWBlzQs4AO2G9kyuAF+0O7bBD1IycRisbOtVktMJBITHMddIcdhMFksFlvlcvmz3W6fxvMz8Bjs2iboMZBDdlKtVpvH+CnY95dDtmw2+0Gv1z/HfAoMsWMqDtrP6rdGKzqdzrIoimsej+cEFl1wuVzHMX2P1Y1Xo1zP2u22aDQaX1UqlS86nW53Pp8fQ1lsGB+B+z4r1Q5VQeVsbTbbG7fb/ZbGDodjpNFoXPL7/afgHgePwKCqoKw1rhUKhY9ms3k6l8vN8TyvjcfjVgg5ZjKZhuGfAHo1QWW7jEy/er3ed5Q1WmbRarUerVar59FC5+C/C25u9vKfllqn4CSbkzc9AJ6AqWQyOSt3CFs7tZn6Wxk1/08QpY8iGo3OkYjkQGkElGJQ7fH7WCOTMOMQ6iQEu0glIAEDgUClXq9/+9dLmi0CkhC3kY2QyWRGKTOaJOFCodAsAn/H4y1wR2lQWrwHgpwOBoNnSH0SKhwO19AVn+Cj2+g6EJRmqsH3P5JOp0cNBsN++sJSqdR7n89XZ/VVdNn01G82mwsbFcbxXzJVh9kVp+hC6QWVrdvtrkYikRnWXgl5odILRcN219IxqXbUMk6ncwZCLGA+AhbBqppfgYYVfcVisbwWBEFbKpXm19VuSUEfS5QQnW6j8xCIM4YU9K9sdB2mqe64yV6wUg38FmAAGmFemcqkrBIAAAAASUVORK5CYII=') no-repeat";
	adButton.style.zIndex=999999999999;
	adButton.style.border="none";
	adButton.style.backgroundRepeat="no-repeat";
	adButton.style.backgroundPosition="center center";
	adButton.id="ad-skip-button";
	adButton.style.display="none";
};

adVPAIDUI.prototype.showSkipButton=function(isShow){
	console.log("SHOW BUTTON -- "+isShow);
	var skipButton=document.getElementById("ad-skip-button");
	if(skipButton!=null)
		skipButton.style.display=isShow?"block":"none";
};

adVPAIDUI.prototype.createProgressBar=function(){
	var progress=document.createElement('div');
	var bar=document.createElement('div');
	progress.appendChild(bar);
	var vpaidContainer=document.getElementById('adview-slot');
	vpaidContainer.appendChild(progress);
	progress.style.position="absolute";
	progress.style.width="100%";
	progress.style.height="3px";
	progress.style.backgroundColor="transparent";
	progress.style.zIndex=1000000;
	progress.style.bottom=0;
	progress.id="ad-progress-lm";
	bar.style.position="absolute";
	bar.style.width="0%";
	bar.style.height="100%";
	bar.style.backgroundColor="rgb(0, 142, 239)";
	bar.style.zIndex=1000000;
	bar.id="ad-bar-lm";
};

adVPAIDUI.prototype.moveProgress=function(currentTime,duration){
	var step=(duration-currentTime)/duration*100;
	var elem=document.getElementById("ad-bar-lm");
	elem.style.width=step+"%";
};

adVPAIDUI.prototype.subscribe=function(aCallback,eventName,aContext){
	var callBack=aCallback.bind(aContext);
	this._eventsCallbacks[eventName]=callBack;
};

adVPAIDUI.prototype.unsubscribe=function(eventName){
	this._eventsCallbacks[eventName]=null;
	};
adVPAIDUI.prototype._callEvent=function(eventType){
	if(eventType in this._eventsCallbacks){
		this._eventsCallbacks[eventType]();
	}
};

adVPAIDUI.prototype.skipAd=function(){
	this._callEvent('AdSkipped');
	};
}
var getUI=function(){
	return new adVPAIDUI();
}
*/


