package com.project.chatgpt.Utils

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import com.google.android.material.textfield.TextInputEditText

class ImageEdittext: TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var mimeTypes: Array<String> = arrayOf("image/png",
        "image/gif",
        "image/jpeg",
        "image/webp")
    private var keyBoardInputCallbackListener: KeyBoardInputCallbackListener? = null

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
        val ic: InputConnection? = super.onCreateInputConnection(editorInfo)
        EditorInfoCompat.setContentMimeTypes(editorInfo, mimeTypes)

        val callback = InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, opts ->
                val lacksPermission = (flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0
                // read and display inputContentInfo asynchronously
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && lacksPermission) {
//                    try {
//                        Log.e("lacksPermission",lacksPermission.toString())
//                        inputContentInfo.requestPermission()
//                    } catch (e: Exception) {
//                        return@OnCommitContentListener false // return false if failed
//                    }
//                }
                var supported = false
            for (mimeType in mimeTypes) {
                if (inputContentInfo.description.hasMimeType(mimeType)) {
                        supported = true
                        break
                    }
                }
                if (!supported) {
                    Log.e("lacksPermission",inputContentInfo.contentUri.toString())
                    return@OnCommitContentListener false
                }
            Log.e("lacksPermission22",inputContentInfo.contentUri.toString())

            keyBoardInputCallbackListener?.onCommitContent(inputContentInfo, flags, opts)
            true
            }
        return InputConnectionCompat.createWrapper(ic!!, editorInfo, callback)
    }

    interface KeyBoardInputCallbackListener { fun onCommitContent(inputContentInfo: InputContentInfoCompat?, flags: Int, opts: Bundle?) }

    fun setKeyBoardInputCallbackListener(keyBoardInputCallbackListener: KeyBoardInputCallbackListener) {
        this.keyBoardInputCallbackListener = keyBoardInputCallbackListener
    }
}