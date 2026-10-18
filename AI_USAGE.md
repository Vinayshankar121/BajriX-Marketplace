# AI Usage

- Tool used: OpenAI Codex.
- Used for: project structure, starter code, API design, and documentation assistance.
- Example of correction: the initial idea used a generic repository method that could fetch a listing without checking the seller. It was changed to always load the listing through `sellerId` and `listingId`, which makes the ownership rule clear and prevents one seller from updating another seller's listing.

