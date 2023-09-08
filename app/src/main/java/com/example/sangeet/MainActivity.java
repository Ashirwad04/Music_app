// Import necessary Android libraries and packages
package com.example.sangeet;

// Import various Android components
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

// Define the main activity class
public class MainActivity extends AppCompatActivity {

    // Declare member variables for various UI elements and player controls
    private MediaPlayer mediaPlayer;
    private TextView songTitleTextView, artistNameTextView;
    private SeekBar seekBar;
    private Button playPauseButton, previousButton, nextButton;
    private Handler handler = new Handler();
    private int currentSongIndex = 0;
    private List<Integer> songs;
    private ListView songListView;

    // Annotation to suppress missing inflated ID warnings
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements by finding them in the layout
        songTitleTextView = findViewById(R.id.song_title);
        artistNameTextView = findViewById(R.id.artist_name);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.play_pause_button);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        songListView = findViewById(R.id.song_list);

        // Create a list of song resources (raw audio files)
        songs = new ArrayList<>();
        songs.add(R.raw.sh);
        songs.add(R.raw.ks);
        songs.add(R.raw.d);
        songs.add(R.raw.g);

        // Create an ArrayAdapter to display song names in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.song_names));
        songListView.setAdapter(adapter);

        // Set a click listener for the song list items
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSongIndex = position;
                playSelectedSong();
            }
        });

        // Create and initialize the MediaPlayer with the first song
        mediaPlayer = MediaPlayer.create(this, songs.get(currentSongIndex));
        seekBar.setMax(mediaPlayer.getDuration());

        // Set a listener to update the seek bar when the user interacts with it
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set a click listener for the play/pause button
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between play and pause when the button is clicked
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseButton.setText("Play");
                } else {
                    mediaPlayer.start();
                    playPauseButton.setText("Pause");
                    updateSeekBar();
                }
            }
        });

        // Set a click listener for the previous button
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the previous song in the list, considering looping at the beginning
                currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size();
                playSelectedSong();
            }
        });

        // Set a click listener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the next song in the list, considering looping at the end
                currentSongIndex = (currentSongIndex + 1) % songs.size();
                playSelectedSong();
            }
        });
    }

    // Helper method to update the seek bar while the song is playing
    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            // Schedule a delayed task to update the seek bar periodically
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 1000); // Update every 1 second
        }
    }

    // Helper method to play the selected song
    private void playSelectedSong() {
        // Stop the current playback
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        // Reset the MediaPlayer and set it to play the selected song
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(MainActivity.this, songs.get(currentSongIndex));

        // Set the seek bar max to the new song's duration
        seekBar.setMax(mediaPlayer.getDuration());

        // Start playing the selected song
        mediaPlayer.start();
        playPauseButton.setText("Pause");
        updateSeekBar();
    }

    // Release the MediaPlayer resources when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
