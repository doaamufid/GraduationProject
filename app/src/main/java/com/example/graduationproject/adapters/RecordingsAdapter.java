package com.example.graduationproject.adapters;

import android.media.MediaPlayer;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.graduationproject.R;
import com.example.graduationproject.models.Recording;

import java.io.IOException;
import java.util.List;

/**
 * أدابتر لعرض قائمة التسجيلات المحفوظة (كلماتي الحلوة).
 * كل عنصر معه زر تشغيل/إيقاف مستقل، وبنوقف أي تشغيل سابق قبل ما نبدأ واحد جديد.
 */
public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private final List<Recording> recordings;
    private MediaPlayer activePlayer;
    private int activePlayingPosition = RecyclerView.NO_POSITION;

    public RecordingsAdapter(List<Recording> recordings) {
        this.recordings = recordings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recording, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recording recording = recordings.get(position);
        holder.phraseText.setText(recording.getPhrase());
        holder.dateText.setText(DateUtils.getRelativeTimeSpanString(recording.getSavedAtMillis()));

        boolean isPlayingThis = position == activePlayingPosition;
        holder.playButton.setImageResource(isPlayingThis ? R.drawable.ic_pause : R.drawable.ic_play);

        holder.playButton.setOnClickListener(v -> togglePlayback(holder, recording, position));
    }

    private void togglePlayback(ViewHolder holder, Recording recording, int position) {
        // لو نفس العنصر شغال حالياً، نوقفه
        if (position == activePlayingPosition) {
            stopActivePlayer();
            notifyItemChanged(position);
            return;
        }

        // نوقف أي تشغيل سابق لعنصر ثاني
        int previousPosition = activePlayingPosition;
        stopActivePlayer();
        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition);
        }

        try {
            activePlayer = new MediaPlayer();
            activePlayer.setDataSource(recording.getFilePath());
            activePlayer.prepare();
            activePlayer.start();
            activePlayingPosition = position;
            activePlayer.setOnCompletionListener(mp -> {
                int finishedPosition = activePlayingPosition;
                stopActivePlayer();
                notifyItemChanged(finishedPosition);
            });
            notifyItemChanged(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopActivePlayer() {
        if (activePlayer != null) {
            try {
                if (activePlayer.isPlaying()) {
                    activePlayer.stop();
                }
            } catch (IllegalStateException ignored) {
            }
            activePlayer.release();
            activePlayer = null;
        }
        activePlayingPosition = RecyclerView.NO_POSITION;
    }

    /** لازم تنادى هاي من onDestroy بالأكتفتي عشان ما يضل صوت شغال بالخلفية */
    public void releasePlayer() {
        stopActivePlayer();
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView phraseText;
        TextView dateText;
        ImageButton playButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            phraseText = itemView.findViewById(R.id.itemPhraseText);
            dateText = itemView.findViewById(R.id.itemDateText);
            playButton = itemView.findViewById(R.id.itemPlayButton);
        }
    }
}
