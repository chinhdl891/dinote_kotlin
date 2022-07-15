package com.bzk.dinoteslite.adapter

import android.content.Context
import android.graphics.Path
import android.nfc.Tag
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemAddTagBinding
import com.bzk.dinoteslite.model.TagModel

private const val TAG = "AddTagAdapter"

class AddTagAdapter(var onAddTag: () -> Unit, var onDeleteTag: (position: Int) -> Unit) :
    RecyclerView.Adapter<AddTagAdapter.AddTagViewHolder>() {
    private var listTagModel: MutableList<TagModel> = mutableListOf<TagModel>()
    private lateinit var mContext: Context

    fun initData(list: MutableList<TagModel>) {
        this.listTagModel = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddTagViewHolder {
        mContext = parent.context
        val binding: ItemAddTagBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_add_tag,
                parent,
                false)
        return AddTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddTagViewHolder, position: Int) {
        val tagModel: TagModel = listTagModel[position]
        holder.bind(tagModel)
        holder.mBinding.lnlAddTag.setBackgroundResource(R.drawable.un_focused_background)
        holder.mBinding.imvTagCancel.visibility = View.INVISIBLE
        holder.onClick()
        holder.onFocus()
        if (holder.mBinding.edtAddTag.text.trim().isNotEmpty()) {
            holder.mBinding.edtAddTag.hint = ""
        } else {
            holder.mBinding.edtAddTag.hint = mContext.getString(R.string.add_tag)
        }

    }

    override fun getItemCount(): Int {
        return listTagModel.size
    }

    inner class AddTagViewHolder(var mBinding: ItemAddTagBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(tagModel: TagModel) {
            mBinding.setVariable(BR.tag, tagModel)
            mBinding.executePendingBindings()
        }

        fun onClick() {
            mBinding.imvTagCancel.setOnClickListener {
                mBinding.edtAddTag.clearFocus()
                onDeleteTag(layoutPosition)
            }

            mBinding.edtAddTag.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "onClick: enter")
                    if (mBinding.edtAddTag.text.toString().isNotEmpty()) {
                        mBinding.edtAddTag.clearFocus()
                        onAddTag()
                    }
                    true
                } else {
                    false
                }
            }
        }

        fun onFocus() {
            mBinding.edtAddTag.onFocusChangeListener =
                View.OnFocusChangeListener { view, b ->
                    if (b) {
                        mBinding.imvTagCancel.visibility = View.VISIBLE
                        mBinding.lnlAddTag.setBackgroundResource(R.drawable.on_focused_background)
                        true
                    } else {
                        mBinding.lnlAddTag.setBackgroundResource(R.drawable.un_focused_background)
                        mBinding.imvTagCancel.visibility = View.INVISIBLE
                        val contentTag = mBinding.edtAddTag.text.toString().trim()
                        if (contentTag.isNotEmpty()) {
                            listTagModel[layoutPosition].contentTag = contentTag
                            mBinding.edtAddTag.hint = ""
                        }
                        false
                    }
                }
        }
    }
}



