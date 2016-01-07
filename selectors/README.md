# Selectors

- Reverse from a perspective of how do I use them in my context
- how to find ids, classes, and test
- example with bootstrap?
- chaining classes .one.and.two
- group :nth-child() and first, last. say it's about selecting elements

## CSS

You are probably already familiar with CSS selectors such as `id`, `class` and
`element`. If you rely on those only when you write your element locator you may
have missed out on the great flexibility that comes with the rest of the css
grammar.

### Element Selector

Selects elements in the current page with the same type.

```html
<body>
  <article>First</article>
  <div>Second</div>
</body>
```

```java
driver.findElement(By.css('article')).getText(); // First
driver.findElement(By.css('div')).getText(); // Second
```

When there's more than one element with the same type, only the first is
returned when you use `findElement` or all of them are returned as a list when
you use `findElements`.


```html
<body>
  <ul>
    <li>Red</li>
    <li>Blue</li>
  </ul>
</body>
```

```java
driver.findElement(By.css('li')).getText(); // Red
driver.findElements(By.css('li')).size(); // 2
```

### Class Selector

Selects all the elements with a given class. When you search for a class in the
current page, the name of the class is prefixed by a `.` dot. Hence if you're
looking for an element with a `description` class, you selector is `.description`.

```html
<body>
  <div class="description">Test</div>
  <div class="date">Today</div>
</body>
```

```java
driver.findElement(By.css('.description')).getText(); // Test
```

The class selector matches all the elements with the same class. It doesn't care
about the element type, or how many classes are attached to that element.

```html
<div class="title">
  <div class="item">One</div>
  <span class="item strong">Two</span>
</div>
```

```java
driver.findElements(By.css('.item')).size(); // 2
```

### ID Selector

Selects the element with the same id. The id selector is very similar to the
class selector except that:

  - its prefix is an `#` hash
  - there can only by one id in the entire page

```html
<body>
  <div id="logo">Stark</div>
</body>
```

```java
driver.findElements(By.css('#logo')).getText(); // Stark
```

If there can only be one id per page, what happens when you search for multiple
elements with the same ids?

```html
<div class="title">
  <div id="item">One</div>
  <span id="item strong">Two</span>
</div>
```

```java
driver.findElements(By.css('#item')).size(); // ??
```

[TODO]

### Combining selectors - Descendant Selector (li a)

Being able to select elements on the page using elements, classes or ids only is
very convenient, but may still not be specific enough. Classes are reused
frequently in more than a single widget and searching for a specific element can
be challenging.

But you can be more specific by using a combination of ids, classes and
elements. As long as you list your ids, classes and/or elements and you match
the hirerachy of the page, you can target a very specific element.

```
<body>
  <div>
    <span class="item">First item</span>
  </div>
  <ul>
    <li class="item">Second item</li>
  </ul>
  <section id="footer">
    <ul>
      <li class="item">Third item</li>
    </ul>
  </section>
</body>
```

```java
driver.findElement(By.css('div .item')).getText(); // First Item
```

Selectors don't have to match the hierarchy of the page exactly. As long as the
first selector contains the following selector, you'll be able to
locate the element in the page.

```java
driver.findElement(By.css('#footer ul .item')).getText(); // Third Item
driver.findElement(By.css('#footer .item')).getText(); // Third Item
```
### Attribute Selector

Attribute selectors let you target an element based on its attributes. In
practise this means you could target an element based on its `href` being
`http://google.come` or if the `type` equals `email`.

The simplest selector is the attribute selector. It doesn't do any comparison,
it only targets element with a specific attribute.

If you want all the `href` on the page, you can say:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href]')).size(); // 2
```

#### Attribute equals

Let's select all the `href`s pointing to Google:

When you want a specific attribute with a certain value, you enclose in brackets
the attribute you want to target and the value it should contain.

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElement(By.css('[href="http://google.com"]')).getText(); // Google
```

#### Attribute Begins With

You can select all the links starting with `https` with the `^=` begins with
operator:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href^="https"]')).getText(); // Facebook
```

#### Attribute Ends With

Similarly, you can select all links ending with `co.uk` using the `$=` attribute
ends operator:

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElements(By.css('[href$="co.uk"]')).getText(); // Google
```

#### Attributes Contains

We've covered matching a value of an attribute by the beginning ot the end
value. You can also match the content of the attribute using `*=` attribute
contains.

```html
<body>
  <a href="http://google.co.uk">Google</a>
  <a href="https://facebook.com">Facebook</a>
</body>
```

```java
driver.findElement(By.css('[href*="google"]')).getText(); // Google
```

#### Attribute Contains Word

When the attribute contains several values, such as in the case of a `class`
attribute - you can target the attribute only if it contains a given word.

The `~=` attribute contains word selector is very similar to Attribute contains you
learn about earlier. The difference is that whereas the attribute contains
peform a search for a substring, attribute contains word looks for a word - i.e.
a word with a space on its left and right position.

```html
<body>
  <div class="bigone">Title</div>
  <div class="one two three">Digits</div>
  <div class="a b c">Letters</div>
</body>
```

```java
driver.findElement(By.css('[class*="one"]')).getText(); // Title
driver.findElement(By.css('[class~="one"]')).getText(); // Digits
```

### Pseudo Classes

### Selecting elements from a list nth-of-type()

Often you want to select an item from a list. There're two ways to go about it:

  1. you could use `findElements` to find all the elements in the list, select
    the right index from the list and perform a new `findElement` to find the
    right element
  2. you could use `:nth-type-of` selector and do everything in css

Option 1 seems straightforward:

```html
<body>
  <h1>Blog</h1>
  <p>This is a list</p>
  <article>
    <h2>Title1</h2>
    <p>Description 1</p>
  </article>
  <article>
    <h2>Title2</h2>
    <p>Description 2</p>
  </article>
  <article>
    <h2>Title2</h2>
    <p>Description 2</p>
  </article>
</body>
```

```java
driver.findElements(By.css("article")).get(1).findElement("p").getText(); // Description 2
```

The only disadvantage is that you can't use that construct in your page object
since you rely on `.get(1)` to select the right index of the array.

An alternative approach is to use CSS only and rely on the `:nth-type-of` pseudo
selector.

The `:nth-of-type` selector allows you to select one or more elements based on
their order in the page.

You can get the second description with:

```java
driver.findElement(By.css("article:nth-of-type(1) p")).getText(); // Description 2
```

It works. But you may be asking, why not using the selector like this `article
p:nth-of-type(1)`?

Because `:nth-of-type` works only when the selected elements are siblings. When
you say `article p:nth-of-type(1)` the selector reads as follow: find an
`article` tag and from there grab the second `p` tag.

The selector fails because there's only one `p` tag.

On the other hand, the following selector `article:nth-of-type(1) p` translates
to: find the second `article` tag in the list and get the `p` tag.

### Selecting active elements :checked, :enable, :disabled

CSS has selectors to target form elements such as checkboxes, radio buttons or
input elements.

When you want to select an element with a `checked` attribute - such as
a checkbox or radio button - you can simply use the `:checked` pseudoclass.

```html
<body>
  <form>
    <input type="checkbox" label="first">
    <input type="checkbox" label="second">
    <input type="checkbox" label="third" checked>
  </form>
</body>
```

```java
driver.findElement(By.css("input:checked")).getAttribute("label"); // second
```

You may have notice that `checked` is also an attribute of the element. If you
recall the earlier section on attribute then you could rewrite the selector as:

```java
driver.findElement(By.css("input[checked]")).getAttribute("label"); // second
```

In other words, the pseudo selector `:checked` is equivalent to the attribute
selector `[checked]`.

Similarly, you can use `:disabled` and `:enabled` to select elements that can
receive the disabled attribute - such as `<button>`, `<input>`, `<textarea>`,
`<optgroup>`, `<option>` and `<fieldset>`.

```html
<body>
  <form action="">
    <input type="text" disabled value="Mr. Smith">
    <input type="sumit" value="Save">
  </form>
</body>
```

```java
driver.findElement(By.css("input:disabled")).getText(); // Mr. Smith
driver.findElement(By.css("input:enabled")).getText(); // Save
```

As per `:checked`, `:disabled` is equivalent to using the attribute selector `[disabled]`:

```
driver.findElement(By.css("input[enabled]")).getText(); // Save
```

There's no attribute for `enabled`, therefore `:enabled` pseudo class is your
only option.

### Universal Selector

The universal selector `*` matches any element. It's useful if you need to
select all the elements within a selector:

```html
<body>
  <article>
    <h1>Title</h1>
    <p>Description</p>
  </article>
</body>
```

```java
driver.findElements(By.css("article *")).size(); // 2
```

You need to be careful, though. The universal selector `*` will match any
element in the hierarchy:

```html
<body>
  <article>
    <h1>Title</h1>
    <p>Description</p>
    <ul>
      <li>Hello</li>
    </ul>
  </article>
</body>
```

```java
driver.findElements(By.css("article *")).size(); // 4
```

### :not

All the selectors so far are about finding and selecting elements you want to be
present on the page. But what if you want your selector to not match
a particular element?

The `:not(x)` pseudo selector finds an element that is not matched by the
selector `x`.

```html
<body>
  <h1>Title</h1>
  <p>Description</p>
</body>
```

```java
driver.findElement(By.css("body:not(h1)")).getText(); // Description
```

You can combine `:not` with other selectors to traverse the page when there's no
match:

```html
<body>
  <article>
    <h1>title1</h1>
    <p>description1</p>
    <a href="link">link1</a>
  </article>
  <article>
    <h1>title2</h1>
    <p>description2</p>
    <a href="link">link2</a>
  </article>
  <div>
    <h1>title3</h1>
    <p>description3</p>
    <a href="link">link3</a>
  </div>
</body>
```

```java
driver.findElement(By.css("body :not(article) h1")).getText(); // title3
```

When you use `:not` you need to be aware the only arguments you can use are:

  - element selector - `a`, `span`, `input`, etc.
  - universal selector - `*`
  - attribute selector - `[enabled]`, `[class^='btn']`, etc.
  - class selector - `.btn-lg`, `.navigation`, etc.
  - id - `#footer`, `#logo`, etc.
  - pseudo class selector - `:checked`, `:enabled`, etc.

## xpath

Using XPath reseable a lot navigating the file system. If you are familiar with
the unix terminal or you've paid attention to the path in Windows' file system
you will have no issue grasping XPath.

### axis specifiers

When you want to select an element from the root context, you can traverse it
with `/`:

```html
<html>
  <body>
    <article>
      <h1>Title</h1>
      <div class="body">
        <h1>Subtitle</h1>
      </div>
    </article>
  </body>
</html>
```

```java
driver.findElement(By.xpath("/html/body/article/h1")).getText(); //Title
driver.findElement(By.xpath("/html/body/article/div/h1")).getText(); // Subtitle
```

Notice how you need to match the structure of the page from the very beginning
(i.e. the `<html>` element) till the element you're interested in. If what
you're after is all the `h1` in the page, you can use the *search or descendat*
axis `//`

```html
<body>
  <article>
    <h1>Title</h1>
    <div class="body">
      <h1>Subtitle</h1>
    </div>
  </article>
</body>
```

```java
driver.findElement(By.xpath("//h1")).getText(); //Title
driver.findElements(By.xpath("//h1")).size(); // 2
```

When you want to target just a particular `h1` element in the page, you can
combine the two axis toghether:

```html
<body>
  <article>
    <h1>Title</h1>
    <div class="body">
      <h1>Subtitle</h1>
    </div>
  </article>
</body>
```

```java
driver.findElement(By.xpath("//div/h1")).getText(); // Subtitle
```

The `//div/h1` selector reads as follow: in the entire page, search for an `h1`
that is a child of a `div`.

If you feel lazy, you can forget the name of the elements and replace it with
a `*`. You still need to match the same hierarchy in the page, but you have the
flexibility to match any element.

```html
<body>
  <article>
    <h1>Title</h1>
    <div class="body">
      <h1>Subtitle</h1>
    </div>
  </article>
</body>
```

```java
driver.findElement(By.xpath("//article/*/h1")).getText(); // Subtitle
```

In this particular case `//article/*/h1` is equivalent to `//article/div/h1`.

When you find an element, sometimes you wish you could select its parent. You
can select an element higher up in the hierarchy using the `..`. Again, this is
very similar to what you would do if you were browsing foders in the terminal.

```html
<body>
  <article class="row">
    <h1>Title</h1>
    <div class="body">
      <h1>Subtitle</h1>
    </div>
  </article>
</body>
```

```java
driver.findElement(By.xpath("//div/..")).getAttribute("class"); // row
```

### tests

Selecting and traversing the page with axes is very powerful, but still too
generic. Most of the time you want to select an element based on things like
classes or its position as a child.

After you have located an element in the page, you can further refine your
selection using tests.

Tests are useful to select an element only when it satifies an expression.  As
an example, you can select the first element in a list of elements. Or an
element containing the word *Save*.

### predicates
### functions and operators

## xpath vs css
