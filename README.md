# Detect-better-call-states-on-Android
Detecting better call states on Android with RINGING, OFFHOOK, and IDLE

### Copied from a question I posted and answered on StackOverflow
-----------------
Link to the question: 

A week ago, for a week I tried to have an app I'm making completely independent detecting call states. I'm on Lollipop 5.1, so I can't use PRECISE_CALL_STATE that exists from Marshmallow upwards. I'm restricted to the usual CALL_STATE_RINGING, OFFHOOK and IDLE.

My extensive research (majority here on StackOverflow) with Google's help made me realize there's possibly no other way of detecting call states without these 3 on Lollipop or lower, or without system permissions on newer Android versions. There are very precise call states on all firmwares. But from a thread here on SO, it seems they can only be used by the current phone app (which I won't replace - will still be the normal Phone app). So no way to use those states in this case, it seems.

With what I wrote on the question in mind, I was forced to use these 3 states, or use the call history. As I wanted the app to be as independent as possible, I tried to make the app detect the PRECISE_CALL_STATEs from itself. Except when there are too many calls. In case they're 3 or more, I must go get the call state of some from the call history (except one case if there are 3 calls, which is explained in the code). I just can't detect calls on hold, sadly.

So far I can detect the following cases:
- Incoming call;
- Incoming call waiting;
- Outgoing call;
- Call just lost;
- Call lost some time ago;
- Call just answered;
- Call answered some time ago;
- Call just finished;
- Call finished some time ago.

And this is the code which I made to make this happen.

I call this from inside TelephonyManager.listen() method, when the phone state changes. That method I have in a constructor of a class which is instanciated on a service that never stops and it's the main service of the entire app. Basically, this object is always on memory. Read method description there to know what to do with it. Hopefully I explained decently. If I didn't, please tell me what I can explain better.

--------------------------------

If anyone has ideas to improve this, please feel free to share your thoughts! A better way than this one that I see is on every phone state change, go see if there was a change in the call history, depending on which change it was. But that would take even more time from me and I don't have enough for that. A week for this was already too much, since I had to borrow my mother, father and brother's phones, and sometimes the house phone to get to this hahaha. And they're not home all the time.

What I tried and didn't work for one or more things:
- Detect if recording of the call is available or not (with or without system permissions and root - works on both cases).
This is not enough, as since an outgoing call is made, the recording gets available to be made, even if the call wasn't answered by the other party yet (or not at all).
Also doesn't work to know if the call is on hold or not, since the recording remains available.
Between hanging up a call and answering a waiting one, the recording is still available to be done, so it doesn't work on that case either.
Basically this seems to be like "recording_available = (callState == OFFHOOK)".
Sum up: it's of no use for anything going this route, it seems.

What I'd like to try but won't for not having time:
- What someone said in a SO post. Get the call stream and see if it's beeping or not. If it is, then it's not answered. If it stopped, it's answered or disconnected (but the last case we can already detect, so it would have been answered).
But this would take infinity more and I'm not doing it unless I make some calling app or something. The one I'm making doesn't need me to go this far, so won't be me doing it.
- On each phone state change, depending on the state, check the changes in the call history for new calls on it. After that, if possible, compare to what should have happened in that change of phone state. If it fits, use it. If not, don't do anything. If not possible to compare, just use anyways.
