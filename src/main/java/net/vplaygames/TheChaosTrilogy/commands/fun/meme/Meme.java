/*
 * Copyright 2020-2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vplaygames.TheChaosTrilogy.commands.fun.meme;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Arrays;

public class Meme {
    public final String postLink;
    public final String subreddit;
    public final String title;
    public final String url;
    public final boolean nsfw;
    public final boolean spoiler;
    public final String author;
    public final int ups;
    public final String[] previews;

    public Meme(String postLink, String subreddit, String title, String url, boolean nsfw, boolean spoiler, String author, int ups, String[] previews) {
        this.postLink = postLink;
        this.subreddit = subreddit;
        this.title = title;
        this.url = url;
        this.nsfw = nsfw;
        this.spoiler = spoiler;
        this.author = author;
        this.ups = ups;
        this.previews = previews;
    }

    public static Meme parse(String json) {
        return parse(DataObject.fromJson(json));
    }

    public static Meme parse(DataObject data) {
        int ups          = data.getInt("ups");
        boolean nsfw     = data.getBoolean("nsfw");
        boolean spoiler  = data.getBoolean("spoiler");
        String postLink  = data.getString("postLink");
        String subreddit = data.getString("subreddit");
        String title     = data.getString("title");
        String url       = data.getString("url");
        String author    = data.getString("author");
        return new Meme(postLink, subreddit, title, url, nsfw, spoiler, author, ups, new String[0]);
    }

    public String toJSONString() {
        return "{\"postLink\":\"" + postLink +
                "\",\"subreddit\":\" " + subreddit +
                "\",\"title\":\"" + title +
                "\",\"url\":\"" + url +
                "\",\"nsfw\":" + nsfw +
                ",\"spoiler\":" + spoiler +
                ",\"author\":\"" + author +
                "\",\"ups\":" + ups +
                ",\"previews\":" + Arrays.deepToString(previews)+"}";
    }
}
