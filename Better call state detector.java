public static final String CALL_PHASE_OUTGOING = "CALL_PHASE_OUTGOING";
public static final String CALL_PHASE_RINGING_NEW = "CALL_PHASE_RINGING_NEW";
public static final String CALL_PHASE_LOST = "CALL_PHASE_LOST";
public static final String CALL_PHASE_LOST_LATE = "CALL_PHASE_LOST_LATE";
public static final String CALL_PHASE_RINGING_WAITING = "CALL_PHASE_RINGING_WAITING";
//public static final String CALL_PHASE_ON_HOLD = "CALL_PHASE_ON_HOLD";
public static final String CALL_PHASE_ANSWERED = "CALL_PHASE_ANSWERED";
public static final String CALL_PHASE_ANSWERED_LATE = "CALL_PHASE_ANSWERED_LATE";
public static final String CALL_PHASE_FINISHED = "CALL_PHASE_FINISHED";
public static final String CALL_PHASE_FINISHED_LATE = "CALL_PHASE_FINISHED_LATE";

public static final String BETTER_CALL_STATE_OUTGOING = "BETTER_CALL_STATE_OUTGOING";
public static final String BETTER_CALL_STATE_INCOMING = "BETTER_CALL_STATE_INCOMING";
public static final String BETTER_CALL_STATE_WAITING = "BETTER_CALL_STATE_WAITING";
public static final String BETTER_CALL_STATE_DISCONNECTED = "BETTER_CALL_STATE_FINISHED";
//public static final String BETTER_CALL_STATE_ON_HOLD = "BETTER_CALL_STATE_ON_HOLD";
public static final String BETTER_CALL_STATE_ACTIVE = "BETTER_CALL_STATE_ACTIVE";
/**
 * <p>This gets the phase of the call when a new phone state is detected (RINGING, OFFHOOK, or IDLE).</p>
 * <p>There are values ending in "_LATE". Those are so because they're only detected after the end of all calls are over and after the phone gets to IDLE state.
 *    Which means, they already happened some time ago (1 second, 10 minutes, unpredictable).</p>
 * <br>
 * <p><b><u>---CONSTANTS---</u></b></p>
 * <p>- <u>CALL_PHASE_OUTGOING [int]</u> --> returned in case an outgoing call was just started (whether it is answered or not by the other party - it's not possible to detect that easily).</p>
 * <p>- <u>CALL_PHASE_RINGING_NEW [int]</u> --> returned in case it's a new incoming call.</p>
 * <p>- <u>CALL_PHASE_LOST [int]</u> --> returned in case the call has just been lost.</p>
 * <p>- <u>CALL_PHASE_LOST_LATE [int]</u> --> returned in case the call was lost some time ago already.</p>
 * <p>- <u>CALL_PHASE_RINGING_WAITING [int]</u> --> returned in case there's a new call which is waiting to be answered (some call is already active).</p>
 * <p>- <u>CALL_PHASE_ANSWERED [int]</u> --> returned in case the call has just been answered.</p>
 * <p>- <u>CALL_PHASE_ANSWERED_LATE [int]</u> --> returned in case the call was answered some time ago alreaedy.</p>
 * <p>- <u>CALL_PHASE_FINISHED [int]</u> --> returned in case the call was just finished (after having been answered - if it wasn't answered, it was LOST or LOST_LATE).</p>
 * <p>- <u>CALL_PHASE_FINISHED_LATE [int]</u> --> returned in case the call was finished some time ago already (the same in parenthesis for FINISHED applies here).</p>
 * <p><b><u>---CONSTANTS---</u></b></p>
 *
 * @param context <u>[Context]</u> --> Context of the application.
 * @param state <u>[int]</u> --> One of the CALL_STATE in TelephonyManager.
 * @param incomingNumber <u>[String]</u> --> Phone number that came with the state change.
 * @param calls_state <u>[ArrayList(ArrayList(String))]</u> --> An ArrayList of the indicated type that will have the list of calls currently in processing
 *                        (put empty in the beginning and keep the object where the array was created in memory, so the contents of the array are kept, or save it somewhere,
 *                        but don't give always an empty one - this method handles cleaning it when needed. Just give it empty in the beginning of the app and let the method
 *                        handle it from there).
 * @param map_CallLog_to_CALL_PHASE <u>[LinkedHashMap(Integer, String)]</u> --> A map with the TYPEs in CallLog.Call on its keys, and on its valus, the corresponding CALL_PHASEs.
 *                                     Example: <br><br>map_CallLog_to_CALL_PHASE.put(CallLog.Calls.INCOMING_TYPE, CALL_PHASE_ANSWERED);
 *                                                  <br>map_CallLog_to_CALL_PHASE.put(CallLog.Calls.MISSED_TYPE, CALL_PHASE_LOST).
 *
 * @return <u>[ String[][] ]</u> --> A double array of Strings in which each element contains the number and the phase call (CALL_PHASE) in which the number is currently in.
 *         There can be more than one event in a state change. It may be understood that a call had already been finished some time ago, or lost some time ago.
 *         Though, the events will always be in the actual event order. If a call was lost before another was answered, then the order will be exactly that one and not the opposite.
 */
public static String[][] get_call_phase(Context context, int state, String incomingNumber, ArrayList<ArrayList<String>> calls_state, LinkedHashMap<Integer, String> map_CallLog_to_CALL_PHASE) {
    switch (state) {
        case (CALL_STATE_RINGING): {
            //System.out.println("RINGING - " + incomingNumber);

            // New incoming call (there are no calls in the current processing call list).
            if (calls_state.size() == 0) { // Which means, was in IDLE.
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(incomingNumber);
                arrayList.add(BETTER_CALL_STATE_INCOMING);
                calls_state.add(arrayList);

                System.out.println(CALL_PHASE_RINGING_NEW + " -> " + incomingNumber);
                return new String[][]{new String[]{incomingNumber, CALL_PHASE_RINGING_NEW}};
            } else {
                // New incoming call waiting
                for (int i = 0; i < calls_state.size(); i++) {
                    if (calls_state.get(i).get(1).equals(BETTER_CALL_STATE_ACTIVE)) {
                        // If any call was already active and another one came, then that other one is waiting to be answered.
                        // This also works with 3 calls, even on case 8, since the state of the 1st call only changes on IDLE.
                        // Until then it remains ACTIVE, even having been already disconnected (don't know a way to detect it was disconnected).
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(incomingNumber);
                        arrayList.add(BETTER_CALL_STATE_WAITING);
                        calls_state.add(arrayList);

                        System.out.println(CALL_PHASE_RINGING_WAITING + " -> " + incomingNumber);
                        return new String[][]{new String[]{incomingNumber, CALL_PHASE_RINGING_WAITING}};
                        //break;
                    }
                }
            }

            break;
        }

        case (CALL_STATE_OFFHOOK): {
            //System.out.println("OFFHOOK - " + incomingNumber);
            String[] to_return = null;
            /*if (calls_state.size() == 0) {
                // If there are no calls in processing (for example, the app was started with at least one call already in course), abort and do nothing at all.
                // Can't have this here... Or it won't detect an outgoing call, which puts the phone in this state in the beginning.
                break;
            }*/

            // Check if it's an outgoing call.
            if (calls_state.size() == 0) { // Ou seja, estava em IDLE.
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(incomingNumber);
                arrayList.add(BETTER_CALL_STATE_OUTGOING);
                calls_state.add(arrayList);

                System.out.println(CALL_PHASE_OUTGOING + " -> " + incomingNumber);
                to_return = new String[]{incomingNumber, CALL_PHASE_OUTGOING};
            } else {
                // Check if the 1st or only call was answered.
                for (int i = 0; i < calls_state.size(); i++) {
                    if (PhoneNumberUtils.compareStrictly(calls_state.get(i).get(0), incomingNumber)) {
                        if (calls_state.get(i).get(1).equals(BETTER_CALL_STATE_INCOMING)) {
                            // If the number was in INCOMING (not WAITING, because I don't know how to detect a call waiting that is answered)
                            // and we are now in the OFFHOOK state, then the call was answered.
                            calls_state.get(i).set(1, BETTER_CALL_STATE_ACTIVE);

                            System.out.println(CALL_PHASE_ANSWERED + " -> " + incomingNumber);
                            return new String[][]{new String[]{incomingNumber, CALL_PHASE_ANSWERED}};
                        }
                    }
                }
            }

            // Add the number to the list with the state OFFHOOK, or update the state in case the number is already on the list.
            // This is for in case the cases above don't apply --> WAITING to OFFHOOK (don't know what to do with that - can't be rejected or answered).
            // Then in that case, I leave the state CALL_STATE_OFFHOOK on the list.
            for (int i = 0; i < calls_state.size(); i++) {
                if (PhoneNumberUtils.compareStrictly(calls_state.get(i).get(0), incomingNumber)) {
                    calls_state.get(i).set(1, String.valueOf(CALL_STATE_OFFHOOK));
                    break;
                }
            }
            return new String[][]{to_return};
            //break;
        }

        case (CALL_STATE_IDLE): {
            //System.out.println("IDLE - " + incomingNumber);
            ArrayList<String[]> final_return = new ArrayList<>();
            if (calls_state.size() == 0) {
                // If there are no calls in processing (for example, the app was started with at least one call already in course), abort and do nothing at all.
                break;
            }

            System.out.println("Aqui:");
            for (int i = 0; i < calls_state.size(); i++) {
                System.out.println(calls_state.get(i).get(0) + " | " + calls_state.get(i).get(1));
            }

            //////////////////////////////////////
            // Beginning of the LATE events

            // We begin by the LATE events for the correct order to go inthe return array of all events, the closes to reality possible.

            // Bellow is the handling of all numbers that didn't came with the state IDLE. Only one can come with the state and is the one for which the call was finished right now or lost right now.
            // The other would get no treatment. Therefore, this tried to understand what may have happened. All here will be of the LATE type because of exactly that (what happened --> past).

            // If it's more than a call, we can apply a "trick" to know the state of the first and of the last - answered or lost. This is an example of the cases 1 and 6 (2 calls) and 7 to 10 (3 calls).
            if (calls_state.size() > 1) {
                // If the first call would have been lost, this would have gone to IDLE directly, and the list would have gotten empty. Then the call coming next would
                // be the first call again. If there is a 2nd, the 1st must have been answered. And if it was answered, it was finished in some moment.

                // In case the 1st call wasn't the one that came in IDLE, then it was finished some time ago already.
                if (!calls_state.get(0).get(0).equals(incomingNumber)) {
                    // In 2 calls, if the 2nd comes on IDLE, then it means the 1st one was already finished a some time ago (because, again, if the 1st wasn't answered,
                    // there would be no 2nd). And this sets that state in the call and returns it.
                    // This can be applied for 3 or more calls too. In that case, if any call not the 1st gets on IDLE, the 1st was already finished some time ago.
                    System.out.println(CALL_PHASE_FINISHED_LATE + " -> " + calls_state.get(0).get(0));
                    final_return.add(new String[]{calls_state.get(0).get(0), CALL_PHASE_FINISHED_LATE});
                    calls_state.get(0).set(1, BETTER_CALL_STATE_DISCONNECTED);
                }
            }

            for (int i = 0; i < calls_state.size(); i++) {
                if (PhoneNumberUtils.compareStrictly(calls_state.get(i).get(0), incomingNumber)) {
                    if (!(calls_state.get(i).get(1).equals(BETTER_CALL_STATE_INCOMING) || calls_state.get(i).get(1).equals(BETTER_CALL_STATE_WAITING))) {
                        // In case the call didn't come from INCOMING or WAITING, then check if it was answered some time ago or not.
                        // Which means, if it wasn't detected the call was answered in the right moment (so it's not in ACTIVE state)...
                        if (!calls_state.get(i).get(1).equals(BETTER_CALL_STATE_ACTIVE)) {
                            System.out.println(CALL_PHASE_ANSWERED_LATE + " -> " + calls_state.get(i).get(0));
                            final_return.add(new String[]{calls_state.get(i).get(0), CALL_PHASE_ANSWERED_LATE}); // ... then it was answered some time ago already.
                        }
                    }
                    break;
                }
            }

            // For 3 or more calls, for all calls in the middle of the 1st and last, it's not possible to know their state without, at least, a way of knowing if any
            // ended in the middle or not. There, it would be possible to know that the 2nd one was answered, for example (in the case of 3 calls). But without that,
            // there's no way of knowing.
            // So, in that case, for the remaining calls, we're forced to go to the phone's call history.
            // This unless the case 9 happens. In that case, we can know the state of the 3 calls.
            // Sum up: this is done for all calls, except the 1st and last, and the one that got to IDLE. Supposing it's the 2nd in the case of 3 calls, nothing it's done.
            // In other cases, the calls that get here, we got get the state from the call history.
            // And this is done here to go in the correct order in the return array. After the handling of the 1st call and before the handling of the last call. And on the LATE events.
            if (calls_state.size() >= 3) {
                for (int i = 1; i < calls_state.size()-1; i++) {
                    if (calls_state.get(i).get(0).equals(incomingNumber)) {
                        continue;
                    }

                    int tipo_chamada = obter_tipo_ultima_chamada_numero(context, calls_state.get(i).get(0));
                    if (tipo_chamada == CallLog.Calls.INCOMING_TYPE || tipo_chamada == CallLog.Calls.MISSED_TYPE) {
                        System.out.println(map_CallLog_to_CALL_PHASE.get(tipo_chamada) + " -> " + calls_state.get(calls_state.size() - 1).get(0));
                        final_return.add(new String[]{calls_state.get(calls_state.size() - 1).get(0), map_CallLog_to_CALL_PHASE.get(tipo_chamada)});
                    }
                    calls_state.get(calls_state.size() - 1).set(1, BETTER_CALL_STATE_DISCONNECTED);
                }
                // TODO Imagine the same person calls, gives up, calls again, I hang up who I was talking with and answered this person.
                //  This will detect the state as the last one (answered), when I didn't answered the 1st time.
                // This should be in real-time detecting the exact states from the call history if it can't get them directly, but I don't have time to think on that.
                // In the app I'm making (an assistant), I don't need all the states when the happen exactly. Only missed calls in the end and inoming calls in the beginning.
            }

            if (calls_state.size() > 1 && !calls_state.get(calls_state.size() - 1).get(0).equals(incomingNumber)) {
                // The detection of the last call, in case it was lost some time ago, works both for 2 as for any other superior number of calls.
                // For only one call it's not necessary, because on that case, we know exactly when it's lost.

                // PS: The call that got to IDLE is never LOST_LATE - either lost right now or finished right now.

                // If the last call on the list got the OFFHOOK state (which means, without knowing if it was answered or lost in the right moment),
                // and didn't get here on IDLE, then by the cases 1 and 6 for 2 calls, and by the cases 7 to 10 for 3 calls, it wasn't answered either,
                // or it would have been that call getting to IDLE itself.
                // Getting the 1st one here on IDLE in the case of 2 calls or any other call in the case of 3 calls, then this last one was lost some time ago.
                if (calls_state.get(calls_state.size() - 1).get(1).equals(String.valueOf(CALL_STATE_OFFHOOK))) {
                    System.out.println(CALL_PHASE_LOST_LATE + " -> " + calls_state.get(calls_state.size() - 1).get(0));
                    final_return.add(new String[]{calls_state.get(calls_state.size() - 1).get(0), CALL_PHASE_LOST_LATE});
                    calls_state.get(calls_state.size() - 1).set(1, BETTER_CALL_STATE_DISCONNECTED);
                }
            }

            // End of LATE events
            //////////////////////////////////////

            // Now processing of the immediate events, so they get all in order (the late ones happened before the immediate ones).
            for (int i = 0; i < calls_state.size(); i++) {
                if (PhoneNumberUtils.compareStrictly(calls_state.get(i).get(0), incomingNumber)) {
                    if (calls_state.get(i).get(1).equals(BETTER_CALL_STATE_INCOMING) || calls_state.get(i).get(1).equals(BETTER_CALL_STATE_WAITING)) {
                        // If it came directly from INCOMING or WAITING states to IDLE, then the call was lost right now.
                        System.out.println(CALL_PHASE_LOST + " -> " + incomingNumber);
                        final_return.add(new String[]{incomingNumber, CALL_PHASE_LOST});
                    } else {
                        // If the state is not INCOMING or WAITING, this in case will be OFFHOOK or ANSWERED. Which means, the call was finished right now (means was alreaedy answered some ago - or would have been lost).
                        // In no case where a call goes from OFFHOOK to IDLE means the call was lost some time ago, from the testing. So the only option is the call having been finished, or lost, right now (which is handled on the above IF).
                        System.out.println(CALL_PHASE_FINISHED + " -> " + incomingNumber);
                        final_return.add(new String[]{incomingNumber, CALL_PHASE_FINISHED});
                    }
                    calls_state.get(i).set(1, BETTER_CALL_STATE_DISCONNECTED);
                    break;
                }
            }

            calls_state.clear();
            return final_return.toArray(new String[0][0]);
            //break;
        }
    }
    return null;
}
