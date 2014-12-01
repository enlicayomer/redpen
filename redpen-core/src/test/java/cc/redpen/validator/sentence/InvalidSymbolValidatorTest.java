/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.Symbol;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static cc.redpen.config.SymbolType.COMMA;
import static cc.redpen.config.SymbolType.EXCLAMATION_MARK;

public class InvalidSymbolValidatorTest {
    @Test
    public void testWithInvalidSymbol() throws RedPenException {
        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("わたしはカラオケが大好き！", 1)
                        .build()).build();

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setLanguage("en")
                .setSymbol(new Symbol(EXCLAMATION_MARK, '!', "！"))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testWithoutInvalidSymbol() throws RedPenException {
        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like Karaoke", 1)
                        .build()).build();

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setLanguage("en")
                .setSymbol(new Symbol(EXCLAMATION_MARK, '!', "！"))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testWithoutMultipleInvalidSymbol() throws RedPenException {

        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("わたしは、カラオケが大好き！", 1) // NOTE: two invalid symbols
                        .build()).build();

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setLanguage("en")
                .setSymbol(new Symbol(EXCLAMATION_MARK, '!', "！"))
                .setSymbol(new Symbol(COMMA, ',', "、"))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.getDocument(0)).size());
    }
}
